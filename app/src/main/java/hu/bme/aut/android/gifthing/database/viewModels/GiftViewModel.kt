package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.repositories.GiftRepository

class GiftViewModel(application: Application) : AndroidViewModel(application) {
    private val mRepository: GiftRepository = GiftRepository(application)
    private val mAllGifts: LiveData<List<Gift>>
    val allGifts: LiveData<List<Gift>>
        get() = mAllGifts

    fun insert(gift: Gift) {
        mRepository.insert(gift)
    }

    init {
        mAllGifts = mRepository.getAllGifts()
    }
}