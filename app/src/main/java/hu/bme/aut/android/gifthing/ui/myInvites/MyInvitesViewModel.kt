package hu.bme.aut.android.gifthing.ui.myInvites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyInvitesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is not implemented"
    }
    val text: LiveData<String> = _text
}