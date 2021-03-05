package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.snakydesign.livedataextensions.emptyLiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.models.dto.AbstractTeamResponse
import hu.bme.aut.android.gifthing.database.models.dto.TeamRequest
import hu.bme.aut.android.gifthing.database.models.entities.Team
import hu.bme.aut.android.gifthing.database.models.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.models.entities.UserTeamCrossRef
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import java.util.concurrent.TimeUnit
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
        return mTeamDao.getTeamWithMembers(id)
    }

    fun create(team: Team, idList: MutableList<Long>): LiveData<Boolean> {
        val result = emptyLiveData<Boolean>()
        thread {
            try {
                val serverIdList = mutableListOf<Long>()
                idList.forEach { serverIdList.add(userRepository.mUserDao.getServerId(it)) }
                val teamRequest = TeamRequest(
                    team.name,
                    team.teamServerId,
                    team.adminId,
                    serverIdList
                )
                val response = teamService.create(teamRequest).execute()
                if (response.isSuccessful) {
                    val createdTeam = response.body()!!
                    val currentTeamId = mTeamDao.insert(createdTeam.toClientTeam())
                    createdTeam.members.forEach { member ->
                        userRepository.saveUserResponse(member)
                        val userClientId =
                            userRepository.mUserDao.getByServerIdNoLiveData(member.id)!!.userClientId
                        mTeamDao.insertUserTeamCross(
                            UserTeamCrossRef(
                                userClientId,
                                currentTeamId
                            )
                        )
                    }
                    result.postValue(true)
                } else {
                    result.postValue(false)
                    throw Exception("create " + response.code())
                }
            } catch (e: Exception) {
                result.postValue(false)
                e.printStackTrace()
            }
        }
        return result
    }

    private fun refreshTeamInDb(team: AbstractTeamResponse) {
        val currentTeam = mTeamDao.getByServerId(team.id)
        if (currentTeam == null) {
            mTeamDao.insert(team.toClientTeam())
        } else {
            val newTeam = team.toClientTeam()
            newTeam.teamClientId = currentTeam.teamClientId
            mTeamDao.update(newTeam)
        }
    }

    fun saveMyTeams() {
        thread {
            try {
                val response = teamService.getMyTeams().execute()
                if (response.isSuccessful) {
                    val teams = response.body()!!
                    teams.forEach { team ->
                        refreshTeamInDb(team)
                        val currentTeamId = mTeamDao.getByServerId(team.id)!!.teamClientId

                        team.members.forEach { member ->
                            userRepository.saveUserResponse(member)
                            val userClientId =
                                userRepository.mUserDao.getByServerIdNoLiveData(member.id)!!.userClientId
                            mTeamDao.insertUserTeamCross(
                                UserTeamCrossRef(
                                    userClientId,
                                    currentTeamId
                                )
                            )
                        }
                    }
                } else {
                    throw  Exception("saveMyTeams " + response.code())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}