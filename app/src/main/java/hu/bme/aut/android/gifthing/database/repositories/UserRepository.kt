package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.snakydesign.livedataextensions.emptyLiveData
import hu.bme.aut.android.gifthing.authentication.dto.LoginRequest
import hu.bme.aut.android.gifthing.authentication.dto.LoginResponse
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.entities.UserWithOwnedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithReservedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams
import hu.bme.aut.android.gifthing.services.AuthService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class UserRepository(val application: Application) : CoroutineScope by MainScope() {
    private val mUserDao: UserDao
    private val mAllUsers: LiveData<List<User>>
    private val mUsername: LiveData<List<String>>
    private val userService = ServiceBuilder.buildService(UserService::class.java)
    private val authService = ServiceBuilder.buildService(AuthService::class.java)

    companion object {
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(10)
    }

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
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

    fun getUserWithOwnedGifts(id: Long): LiveData<UserWithOwnedGifts> {
        refreshUser(id)
        return mUserDao.getUserWithOwnedGifts(id)
    }

    fun getUserWithReservedGifts(id: Long): LiveData<UserWithReservedGifts> {
        refreshUser(id)
        return mUserDao.getUserWithReservedGifts(id)
    }

    fun getUserWithTeams(id: Long): LiveData<UserWithTeams> {
        refreshUser(id)
        return mUserDao.getUserWithTeams(id)
    }

    fun create(user: User) {
        AppDatabase.databaseWriteExecutor.execute { mUserDao.insert(user) }
    }

    fun getByServerId(serverId: Long): LiveData<User> {
        val resultUser = emptyLiveData<User>()
        thread {
            var exists = false
            val users = mUserDao.getAllUser()
            for (user in users) {
                if (user.userServerId == serverId) {
                    exists = true
                    resultUser.postValue(user)
                }
            }

            if (!exists)
                userService.getById(serverId)
                    .enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.User> {
                        override fun onResponse(
                            call: Call<hu.bme.aut.android.gifthing.database.models.User>,
                            response: Response<hu.bme.aut.android.gifthing.database.models.User>
                        ) {
                            if (response.isSuccessful) {
                                val result = toUser(response)
                                val giftList = response.body()!!.gifts
                                val ownedTeamList = response.body()!!.myOwnedTeams
                                val myTeamList = response.body()!!.myTeams
                                val giftRepository = GiftRepository(application)
                                val teamRepository = TeamRepository(application)
                                giftRepository.refreshGiftList(giftList)
                                teamRepository.refreshOwnedTeamList(ownedTeamList)
                                teamRepository.refreshMyTeamList(myTeamList)
                                AppDatabase.databaseWriteExecutor.execute {
                                    mUserDao.insert(result)
                                }
                                //TODO: lehet később tér vissza az execute mint ez lefut
                                resultUser.postValue(mUserDao.getByServerId(result.userServerId!!).value)

                            }
                        }

                        override fun onFailure(
                            call: Call<hu.bme.aut.android.gifthing.database.models.User>,
                            t: Throwable
                        ) {
                            //TODO() do nothing? return from old data
                            println("wait what?")
                            throw t
                        }
                    })
        }
        return resultUser
    }

    fun refreshUser(userId: Long) {
        Thread(Runnable {
            val lastFetch = mUserDao.getLastFetch(userId)
            if (lastFetch + FRESH_TIMEOUT < System.currentTimeMillis()) {
                val userServerId = mUserDao.getServerId(userId)
                userService.getById(userServerId)
                    .enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.User> {
                        override fun onResponse(
                            call: Call<hu.bme.aut.android.gifthing.database.models.User>,
                            response: Response<hu.bme.aut.android.gifthing.database.models.User>
                        ) {
                            if (response.isSuccessful) {
                                val result = toUser(response)
                                val giftList = response.body()!!.gifts
                                val ownedTeamList = response.body()!!.myOwnedTeams
                                val myTeamList = response.body()!!.myTeams
                                val giftRepository = GiftRepository(application)
                                val teamRepository = TeamRepository(application)
                                giftRepository.refreshGiftList(giftList)
                                teamRepository.refreshOwnedTeamList(ownedTeamList)
                                teamRepository.refreshMyTeamList(myTeamList)
                                result.userClientId = userId
                                AppDatabase.databaseWriteExecutor.execute { mUserDao.update(result) }
                            }
                        }

                        override fun onFailure(
                            call: Call<hu.bme.aut.android.gifthing.database.models.User>,
                            t: Throwable
                        ) {
                            //TODO() do nothing? return from old data
                        }
                    })

            }
        }).start()
    }

    private fun toUser(response: Response<hu.bme.aut.android.gifthing.database.models.User>): User {
        return User(
            email = response.body()!!.email,
            username = response.body()!!.username,
            firstName = response.body()!!.firstName,
            lastName = response.body()!!.lastName,
            userServerId = response.body()!!.id,
            lastUpdate = response.body()!!.lastUpdate!!,
            lastFetch = System.currentTimeMillis()
        )
    }

    fun login(username: String, password: String): LiveData<LoginResponse> {
        val result = MutableLiveData<LoginResponse>()
        thread {
            authService.login(LoginRequest(username, password))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            result.value = response.body()
                            //TODO: check if exists
                        }
                    }

                    override fun onFailure(
                        call: Call<LoginResponse>,
                        t: Throwable
                    ) {
                        throw t
                    }
                })
        }
        return result
    }

    fun getClientId(currentId: Long): LiveData<Long> {
        return mUserDao.getClientId(currentId)
    }
}