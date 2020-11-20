package hu.bme.aut.android.gifthing.database.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import hu.bme.aut.android.gifthing.database.models.entities.GiftWithOwner
import hu.bme.aut.android.gifthing.database.repositories.GiftRepository

class GiftViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val giftRepository: GiftRepository
): ViewModel() {

    /*
    val giftId : Long = savedStateHandle["gid"] ?:
    throw IllegalArgumentException("missing gift id")
    val gift : LiveData<Gift> = giftRepository.getById(giftId)
     */

    fun create(gift: Gift) {
        giftRepository.create(gift)
    }

    fun delete(giftId: Long) {
        giftRepository.delete(giftId)
    }

    fun getById(giftId: Long): LiveData<Gift> {
        return giftRepository.getById(giftId)
    }

    fun reserve(gift: Gift) {
        return giftRepository.reserve(gift)
    }

    fun release(gift: Gift) {
        return giftRepository.release(gift)
    }

    fun getByIdWithOwner(giftId: Long): LiveData<GiftWithOwner> {
        return giftRepository.getByIdWithOwner(giftId)
    }
}