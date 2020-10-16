package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.entities.UserWithOwnedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithReservedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams

class UserRepository(application: Application) {
    private val mUserDao: UserDao
    private val mAllUsers: LiveData<List<User>>
    private val mUsername: LiveData<List<String>>
    private val mCurrentUser: LiveData<User>

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
        mAllUsers = mUserDao.getAll()
        mUsername = mUserDao.getAllUsername()
        mCurrentUser = mUserDao.getCurrentUser(AppPreferences.currentId!!)
    }

    fun getAllUsers(): LiveData<List<User>> {
        return mAllUsers
    }

    fun getUsername(): LiveData<List<String>> {
        return mUsername
    }

    fun getCurrentUser(): LiveData<User> {
        return mCurrentUser
    }

    fun getById(id: Long): LiveData<User> {
        return mUserDao.getById(id)
    }

    fun getUserWithOwnedGifts(id: Long): LiveData<UserWithOwnedGifts> {
        return mUserDao.getUserWithOwnedGifts(id)
    }

    fun getUserWithReservedGifts(id: Long): LiveData<UserWithReservedGifts> {
        return mUserDao.getUserWithReservedGifts(id)
    }

    fun getUserWithTeams(id: Long): LiveData<UserWithTeams> {
        return mUserDao.getUserWithTeams(id)
    }

    fun insert(user: User) {
        AppDatabase.databaseWriteExecutor.execute { mUserDao.insert(user) }
    }
}