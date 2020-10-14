package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams
import hu.bme.aut.android.gifthing.database.repositories.TeamRepository
import hu.bme.aut.android.gifthing.database.repositories.UserRepository

class TeamViewModel(application: Application) : AndroidViewModel(application) {
    private val mTeamRepository: TeamRepository = TeamRepository(application)
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mAllTeams: LiveData<List<Team>>
    private val mAllTeamWithMembers: LiveData<List<TeamWithMembers>>
    private val mAllUserWithTeams: LiveData<List<UserWithTeams>>
    val allTeams: LiveData<List<Team>>
        get() = mAllTeams

    val allTeamWithMembers: LiveData<List<TeamWithMembers>>
        get() = mAllTeamWithMembers

    val allUserWithTeams: LiveData<List<UserWithTeams>>
        get() = mAllUserWithTeams

    fun insert(team: Team) {
        mTeamRepository.insert(team)
    }

    init {
        mAllTeams = mTeamRepository.getAllTeams()
        mAllTeamWithMembers = mTeamRepository.getTeamWithMembers()
        mAllUserWithTeams = mUserRepository.getUserWithTeams()
    }
}