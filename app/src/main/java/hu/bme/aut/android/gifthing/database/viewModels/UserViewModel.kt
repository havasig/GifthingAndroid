package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.entities.UserWithOwnedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithReservedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams
import hu.bme.aut.android.gifthing.database.repositories.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: UserRepository = UserRepository(application)
    private val mAllUsers: LiveData<List<User>>
    private val mUsername: LiveData<List<String>>
    private val mCurrentUser: LiveData<User>
    private val mMyTeams: LiveData<UserWithTeams>

    val myTeams: LiveData<UserWithTeams>
        get() = mMyTeams

    val allUsers: LiveData<List<User>>
        get() = mAllUsers

    val currentUser: LiveData<User>
        get() = mCurrentUser

    val username: LiveData<List<String>>
        get() = mUsername


    fun insert(user: User) {
        mRepository.insert(user)
    }

    fun getUserWithOwnedGifts(id: Long): LiveData<UserWithOwnedGifts> {
        return mRepository.getUserWithOwnedGifts(id)
    }

    fun getUserWithReservedGifts(id: Long): LiveData<UserWithReservedGifts> {
        return mRepository.getUserWithReservedGifts(id)
    }

    fun getById(id: Long): LiveData<User> {
        return mRepository.getById(id)
    }

    init {
        mAllUsers = mRepository.getAllUsers()
        mUsername = mRepository.getUsername()
        mCurrentUser = mRepository.getCurrentUser()
        mMyTeams = mRepository.getUserWithTeams(AppPreferences.currentId!!)
    }
}