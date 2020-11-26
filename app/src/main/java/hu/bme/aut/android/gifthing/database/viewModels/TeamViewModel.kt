package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.models.entities.Team
import hu.bme.aut.android.gifthing.database.models.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.repositories.TeamRepository

class TeamViewModel(application: Application) : AndroidViewModel(application) {
    private val mTeamRepository: TeamRepository = TeamRepository(application)
    private val mAllTeams: LiveData<List<Team>>

    fun create(team: Team, idList: MutableList<Long>): LiveData<Boolean> {
        return mTeamRepository.create(team, idList)
    }

    fun getTeamWithMembers(id: Long): LiveData<TeamWithMembers> {
        return mTeamRepository.getTeamWithMembers(id)
    }

    init {
        mAllTeams = mTeamRepository.getAllTeams()
    }
}