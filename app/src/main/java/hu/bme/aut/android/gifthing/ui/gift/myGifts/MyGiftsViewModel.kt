package hu.bme.aut.android.gifthing.ui.gift.myGifts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyGiftsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is MyGifts Fragment"
    }
    val text: LiveData<String> = _text
}