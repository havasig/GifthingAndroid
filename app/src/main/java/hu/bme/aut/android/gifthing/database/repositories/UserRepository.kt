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

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
        mAllUsers = mUserDao.getAll()
    }

    fun getAllUsers(): LiveData<List<User>> {
        return mAllUsers
    }

    fun getUserById(id: Long): LiveData<User> {
        return mUserDao.getById(id)
    }

    fun getUserWithOwnedGifts(): LiveData<List<UserWithOwnedGifts>> {
        return mUserDao.getUserWithOwnedGifts()
    }

    fun getUserWithReservedGifts(): LiveData<List<UserWithReservedGifts>> {
        return mUserDao.getUserWithReservedGifts()
    }

    fun getUserWithTeams(): LiveData<List<UserWithTeams>> {
        return mUserDao.getUserWithTeams()
    }

    fun insert(user: User) {
        AppDatabase.databaseWriteExecutor.execute { mUserDao.insert(user) }
    }

    fun meWithOwnedGifts() : LiveData<UserWithOwnedGifts> {
        return mUserDao.getMeWithOwnedGifts(AppPreferences.currentId!!)
    }
}