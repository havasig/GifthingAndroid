package hu.bme.aut.android.gifthing.database.repositories

import android.accounts.NetworkErrorException
import android.app.Application
import android.net.nsd.NsdManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.authentication.dto.LoginRequest
import hu.bme.aut.android.gifthing.authentication.dto.LoginResponse
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.models.dto.GiftResponse
import hu.bme.aut.android.gifthing.database.models.dto.UserResponse
import hu.bme.aut.android.gifthing.database.models.entities.*
import hu.bme.aut.android.gifthing.services.AuthService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class UserRepository(
    val application: Application
    //val giftRepository: GiftRepository
//val teamRepository
) {
    val mUserDao: UserDao
    private val mGiftDao: GiftDao
    private val mTeamDao: TeamDao
    private val mUsername: LiveData<List<String>>
    private val userService = ServiceBuilder.buildService(UserService::class.java)
    private val authService = ServiceBuilder.buildService(AuthService::class.java)
    private val giftRepository = GiftRepository(application)

    companion object {
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(1) //TODO: set to 10 mins
    }

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
        mGiftDao = db.giftDao()
        mTeamDao = db.teamDao()
        mUsername = mUserDao.getAllUsername()
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

    fun getAllUsers(): LiveData<List<User>> {
        return mUserDao.getAllUsers()
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

    fun saveUserResponse(responseBody: UserResponse) {
        // save/update user
        val currentUser = mUserDao.getByServerIdNoLiveData(responseBody.id)
        if (currentUser == null) {
            mUserDao.insert(responseBody.toClientUser())
        } else {
            val newUser = responseBody.toClientUser()
            newUser.userClientId = currentUser.userClientId
            mUserDao.update(newUser)
        }

        // save/update gifts
        val giftList = mutableListOf<GiftResponse>()
        responseBody.gifts.forEach { giftList.add(it) }
        responseBody.reservedGifts.forEach { if(!giftList.contains(it)) giftList.add(it) }

        giftRepository.refreshGiftList(giftList)

    }

    private fun getByIdFromServer(userServerId: Long) {
        try {
            val response = userService.getById(userServerId).execute()
            if (response.isSuccessful) {
                saveUserResponse(response.body()!!)
            } else {
                throw  Exception("getByIdFromServer: " + response.code())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refreshUser(userId: Long, isServerId: Boolean = false) {
        thread {
            val teamRepository = TeamRepository(application)
            val userServerId: Long = if (isServerId) userId else mUserDao.getServerId(userId)
            val lastFetch = mUserDao.getLastFetch(userServerId)
            if (lastFetch == null || lastFetch + FRESH_TIMEOUT < System.currentTimeMillis()) {
                getByIdFromServer(userServerId)
                if (AppPreferences.currentServerId!! == userServerId) {
                    teamRepository.saveMyTeams()
                }
            }
        }.join()
    }

    @Throws(NetworkErrorException::class)
    fun login(username: String, password: String): LiveData<LoginResponse> {
        val result = MutableLiveData<LoginResponse>()
        thread {
            try {
                val response = authService.login(LoginRequest(username, password)).execute()
                if (response.isSuccessful) {
                    result.postValue(response.body())
                    AppPreferences.token = response.body()!!.accessToken
                    AppPreferences.currentServerId = response.body()!!.id
                } else {
                    result.postValue(LoginResponse())
                }
            } catch (e: IOException) {
                val tmp = LoginResponse()
                tmp.id = -2
                result.postValue(tmp)
            }
        }
        return result
    }
}