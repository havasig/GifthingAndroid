package hu.bme.aut.android.gifthing.ui.myGroups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyGroupsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is MyGroups Fragment"
    }
    val text: LiveData<String> = _text
}