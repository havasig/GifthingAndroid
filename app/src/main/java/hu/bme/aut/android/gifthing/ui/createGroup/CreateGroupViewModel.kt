package hu.bme.aut.android.gifthing.ui.createGroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateGroupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is CreateGroup Fragment"
    }
    val text: LiveData<String> = _text
}