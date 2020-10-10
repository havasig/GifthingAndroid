package hu.bme.aut.android.gifthing.database.repositories
import android.app.Application
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.TeamDao
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.entities.*

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


    fun getTeamWithMembers(): LiveData<List<TeamWithMembers>> {
        return mTeamDao.getTeamWithMembers()
    }

    fun insert(team: Team) {
        AppDatabase.databaseWriteExecutor.execute { mTeamDao.insert(team) }
    }
}