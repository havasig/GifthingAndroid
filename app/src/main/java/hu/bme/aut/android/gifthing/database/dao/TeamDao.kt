package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.models.entities.Team
import hu.bme.aut.android.gifthing.database.models.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.models.entities.User
import hu.bme.aut.android.gifthing.database.models.entities.UserTeamCrossRef


@Dao
interface TeamDao {
    @Query("SELECT * FROM team_table")
    fun getAll(): LiveData<List<Team>>

    @Query("SELECT * FROM team_table")
    fun getAllTeams(): List<Team>

    @Query("SELECT * FROM team_table WHERE team_server_id IN (:teamId)")
    fun getByServerId(teamId: Long): Team?

    @Query("SELECT * FROM team_table")
    fun getAllForInsert(): List<Team>

    @Query("SELECT * FROM team_table WHERE team_client_id IN (:teamIds)")
    fun loadAllByIds(teamIds: LongArray): LiveData<List<Team>>

    @Insert
    fun insertAll(vararg gifts: Team)

    @Delete
    fun delete(team: Team)

    @Query("DELETE FROM team_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(team: Team): Long

    @Update
    fun update(team: Team)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWithMembers(team: Team, members: List<User>) //TODO: ??? ez így jó?

    @Transaction
    @Query("SELECT * FROM team_table WHERE team_client_id IN (:teamId)")
    fun getTeamWithMembers(teamId: Long): LiveData<TeamWithMembers>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserTeamCross(userTeamCrossRef: UserTeamCrossRef)

    @Query("SELECT team_client_id FROM team_table WHERE team_server_id IN (:teamId)")
    fun getClientId(teamId: Long): LiveData<Long>

    @Query("SELECT team_table.last_fetch FROM team_table WHERE team_client_id == :teamId")
    fun getLastFetch(teamId: Long): Long?

    @Query("SELECT team_server_id FROM team_table WHERE team_client_id IN (:teamId)")
    fun getServerId(teamId: Long): Long

}