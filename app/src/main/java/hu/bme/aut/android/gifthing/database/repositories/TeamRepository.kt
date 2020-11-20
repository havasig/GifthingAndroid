package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.models.dto.AbstractTeamResponse
import hu.bme.aut.android.gifthing.database.models.dto.TeamResponse
import hu.bme.aut.android.gifthing.database.models.dto.TeamUserResponse
import hu.bme.aut.android.gifthing.database.models.entities.Team
import hu.bme.aut.android.gifthing.database.models.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.models.entities.UserTeamCrossRef
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import kotlin.concurrent.thread

class TeamRepository(application: Application) {
    private val mTeamDao: TeamDao
    private val mAllTeams: LiveData<List<Team>>
    private val teamService = ServiceBuilder.buildService(TeamService::class.java)
    private val userRepository = UserRepository(application)

    init {
        val db: AppDatabase = AppDatabase.getDatabase(application)
        mTeamDao = db.teamDao()
        mAllTeams = mTeamDao.getAll()
    }

    fun getAllTeams(): LiveData<List<Team>> {
        return mAllTeams
    }

    fun getTeamWithMembers(id: Long): LiveData<TeamWithMembers> {
        refreshTeamMembers(id, false)
        return mTeamDao.getTeamWithMembers(id)
    }

    fun insert(team: Team, idList: List<Long>) {
        AppDatabase.databaseWriteExecutor.execute {
            val teamId = mTeamDao.insert(team)
            idList.forEach { userId ->
                mTeamDao.insertUserTeamCross(UserTeamCrossRef(userId, teamId))
            }
        }
    }

    private fun findTeamInDb(teamServerId: Long): Team? {
        val allTeams = mTeamDao.getAllTeams()
        for (team in allTeams) {
            if (team.teamServerId == teamServerId) {
                return team
            }
        }
        return null
    }

    private fun refreshTeamInDb(team: AbstractTeamResponse) {
        if (findTeamInDb(team.id) == null) {
            mTeamDao.insert(team.toClientTeam())
        } else {
            mTeamDao.update(team.toClientTeam())
        }
    }

    fun refreshTeamList(teamUserResponseList: MutableList<TeamUserResponse>) {
        teamUserResponseList.forEach { team ->
            saveTeamUserResponse(team)
        }
    }

    private fun saveTeamUserResponse(teamUserResponse: TeamUserResponse) {
        refreshTeamInDb(teamUserResponse)
        val clientTeam = findTeamInDb(teamUserResponse.id)
        for (memberId in teamUserResponse.memberIds) {
            userRepository.getByServerIdForTeamRepository(memberId)?.let {
                mTeamDao.insertUserTeamCross(
                    UserTeamCrossRef(
                        it.userClientId,
                        clientTeam!!.teamClientId
                    )
                )
            }
        }
    }

    private fun saveTeamResponse(teamResponse: TeamResponse) {
        refreshTeamInDb(teamResponse)
        val clientTeam = findTeamInDb(teamResponse.id)
        for (member in teamResponse.members) {
            val user = userRepository.getByServerIdForTeamRepository(member.id)
            mTeamDao.insertUserTeamCross(
                UserTeamCrossRef(
                    user!!.userClientId,
                    clientTeam!!.teamClientId
                )
            )
        }
    }

    private fun getByIdFromServer(teamServerId: Long) {
        val response = teamService.getById(teamServerId).execute()
        if (response.isSuccessful) {
            saveTeamResponse(response.body()!!)
        } else {
            throw  Exception("todo")
        }
    }

    private fun refreshTeamMembers(teamId: Long, isServerId: Boolean = false) {
        thread {
            val lastFetch = mTeamDao.getLastFetch(teamId)
            if (lastFetch == null || lastFetch + UserRepository.FRESH_TIMEOUT < System.currentTimeMillis()) {
                val teamServerId: Long = if (isServerId) teamId else mTeamDao.getServerId(teamId)
                getByIdFromServer(teamServerId)
            }
        }.join()
    }
}