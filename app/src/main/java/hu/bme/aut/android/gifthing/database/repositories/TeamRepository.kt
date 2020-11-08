package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.entities.UserTeamCrossRef

class TeamRepository(application: Application) {
    private val mTeamDao: TeamDao
    private val mAllTeams: LiveData<List<Team>>

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

    fun insert(team: Team, idList: List<Long>) {
        AppDatabase.databaseWriteExecutor.execute {
            val teamId = mTeamDao.insert(team)
            idList.forEach { userId ->
                mTeamDao.insertUserTeamCross(UserTeamCrossRef(userId, teamId))
            }
        }
    }

    fun refreshOwnedTeamList(ownedTeamList: MutableList<hu.bme.aut.android.gifthing.database.models.Team>) {
        //TODO: implement
    }

    fun refreshMyTeamList(myTeamList: MutableList<hu.bme.aut.android.gifthing.database.models.Team>) {
        //TODO: implement
    }
}