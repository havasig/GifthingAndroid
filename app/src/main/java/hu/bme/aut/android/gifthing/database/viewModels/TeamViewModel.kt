package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams
import hu.bme.aut.android.gifthing.database.repositories.TeamRepository
import hu.bme.aut.android.gifthing.database.repositories.UserRepository

class TeamViewModel(application: Application) : AndroidViewModel(application) {
    private val mTeamRepository: TeamRepository = TeamRepository(application)
    private val mAllTeams: LiveData<List<Team>>

    fun insert(team: Team, idList: List<Long>) {
        mTeamRepository.insert(team, idList)
    }
    fun getTeamWithMembers(id: Long): LiveData<TeamWithMembers> {
        return mTeamRepository.getTeamWithMembers(id)
    }

    init {
        mAllTeams = mTeamRepository.getAllTeams()
    }
}