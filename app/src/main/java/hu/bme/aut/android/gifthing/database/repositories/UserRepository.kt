package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.authentication.dto.LoginRequest
import hu.bme.aut.android.gifthing.authentication.dto.LoginResponse
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.models.dto.TeamUserResponse
import hu.bme.aut.android.gifthing.database.models.dto.UserResponse
import hu.bme.aut.android.gifthing.database.models.entities.*
import hu.bme.aut.android.gifthing.services.AuthService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class UserRepository(
    val application: Application
    //val giftRepository: GiftRepository
//val teamRepository
) {
    private val mUserDao: UserDao
    private val mGiftDao: GiftDao
    private val mTeamDao: TeamDao
    private val mAllUsers: LiveData<List<User>>
    private val mUsername: LiveData<List<String>>
    private val userService = ServiceBuilder.buildService(UserService::class.java)
    private val authService = ServiceBuilder.buildService(AuthService::class.java)

    companion object {
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(10) //TODO: set to 10 mins
    }

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
        mGiftDao = db.giftDao()
        mTeamDao = db.teamDao()
        mAllUsers = mUserDao.getAll()
        mUsername = mUserDao.getAllUsername()
    }

    //TODO: refresh all users in 10 minutes anyway? (and here)
    fun getAllUsers(): LiveData<List<User>> {
        return mAllUsers
    }

    //TODO: refresh all users in 10 minutes anyway? (and here)
    fun getUsername(): LiveData<List<String>> {
        return mUsername
    }

    fun getById(id: Long): LiveData<User> {
        refreshUser(id)
        return mUserDao.getById(id)
    }

    fun getByServerId(id: Long): LiveData<User> {
        refreshUser(id, true)
        return mUserDao.getByServerId(id)
    }

    fun getByServerIdForTeamRepository(id: Long): User? {
        refreshUser(id, isServerId = true, withTeam = false)
        return mUserDao.getByServerIdNoLiveData(id)
    }

    fun getUserWithOwnedGifts(id: Long): LiveData<UserWithOwnedGifts> {
        refreshUser(id)
        return mUserDao.getUserWithOwnedGifts(id)
    }

    fun getUserWithReservedGifts(id: Long): LiveData<UserWithReservedGifts> {
        refreshUser(id)
        return mUserDao.getUserWithReservedGifts(id)
    }

    fun getUserWithTeams(id: Long): LiveData<UserWithTeams> {
        refreshUser(id, false)
        return mUserDao.getUserWithTeams(id)
    }

    fun getUserWithOwnedTeams(id: Long): LiveData<UserWithOwnedTeams> {
        refreshUser(id, false)
        return mUserDao.getUserWithOwnedTeams(id)
    }

    fun create(user: User) {
        AppDatabase.databaseWriteExecutor.execute { mUserDao.insert(user) }
    }

    private fun findUserInDb(userServerId: Long): User? {
        val allUsers = mUserDao.getAllUsers()
        for (user in allUsers) {
            if (user.userServerId == userServerId) {
                return user
            }
        }
        return null
    }

    private fun saveUserResponse(responseBody: UserResponse) {
        val giftList = responseBody.gifts
        val teamList = mutableListOf<TeamUserResponse>()
        responseBody.myTeams.forEach {
            teamList.add(it)
        }
        responseBody.myOwnedTeams.forEach {
            teamList.add(it)
        }
        val giftRepository = GiftRepository(application)
        val teamRepository = TeamRepository(application)
        //TODO giftlist giftRepository.refreshGiftList(giftList)
        teamRepository.refreshTeamList(teamList)

        if (findUserInDb(responseBody.id) == null) {
            mUserDao.insert(responseBody.toClientUser())
        } else {
            mUserDao.update(responseBody.toClientUser())
        }
    }

    private fun saveOnlyUserResponse(responseBody: UserResponse) {
        if (findUserInDb(responseBody.id) == null) {
            mUserDao.insert(responseBody.toClientUser())
        } else {
            mUserDao.update(responseBody.toClientUser())
        }
    }

    private fun getByIdFromServer(userServerId: Long, withTeam: Boolean = true) {
        try {
            val response = userService.getById(userServerId).execute()
            if (response.isSuccessful) {
                if (withTeam)
                    saveUserResponse(response.body()!!)
                else
                    saveOnlyUserResponse(response.body()!!)
            } else {
                throw  Exception("getByIdFromServer")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refreshUser(userId: Long, isServerId: Boolean = false, withTeam: Boolean = true) {
        thread {
            val lastFetch = mUserDao.getLastFetch(userId)
            if (lastFetch == null || lastFetch + FRESH_TIMEOUT < System.currentTimeMillis()) {
                val userServerId: Long = if (isServerId) userId else mUserDao.getServerId(userId)
                getByIdFromServer(userServerId, withTeam)
            }
        }.join()
    }

    fun login(username: String, password: String): LiveData<LoginResponse> {
        val result = MutableLiveData<LoginResponse>()
        thread {
            val response = authService.login(LoginRequest(username, password)).execute()
            if (response.isSuccessful) {
                result.postValue(response.body())
                AppPreferences.token = response.body()!!.accessToken
            } else {
                throw  Exception("login")
            }
        }
        return result
    }
}