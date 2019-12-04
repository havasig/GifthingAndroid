package hu.bme.aut.android.gifthing.ui.team.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateTeamViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is CreateTeam Fragment"
    }
    val text: LiveData<String> = _text
}