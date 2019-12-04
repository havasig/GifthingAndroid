package hu.bme.aut.android.gifthing.ui.gift.reservedGifts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservedGiftsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ReservedGifts Fragment"
    }
    val text: LiveData<String> = _text
}