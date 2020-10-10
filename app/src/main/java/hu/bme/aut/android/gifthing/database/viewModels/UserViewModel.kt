package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.entities.UserWithOwnedGifts
import hu.bme.aut.android.gifthing.database.entities.UserWithReservedGifts
import hu.bme.aut.android.gifthing.database.repositories.GiftRepository
import hu.bme.aut.android.gifthing.database.repositories.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: UserRepository = UserRepository(application)
    private val mAllUsers: LiveData<List<User>>
    private val mAllUsersWithOwnedGifts: LiveData<List<UserWithOwnedGifts>>
    private val mAllUsersWithReservedGifts: LiveData<List<UserWithReservedGifts>>
    val allUsers: LiveData<List<User>>
        get() = mAllUsers

    val allUsersWithOwnedGifts: LiveData<List<UserWithOwnedGifts>>
        get() = mAllUsersWithOwnedGifts

    val allUsersWithReservedGifts: LiveData<List<UserWithReservedGifts>>
        get() = mAllUsersWithReservedGifts

    fun insert(user: User) {
        mRepository.insert(user)
    }

    init {
        mAllUsers = mRepository.getAllUsers()
        mAllUsersWithOwnedGifts = mRepository.getUserWithOwnedGifts()
        mAllUsersWithReservedGifts = mRepository.getUserWithReservedGifts()
    }
}