package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.TeamWithMembers


@Dao
interface TeamDao {
    @Query("SELECT * FROM team_table")
    fun getAll(): LiveData<List<Team>>

    @Query("SELECT * FROM team_table WHERE teamId IN (:teamIds)")
    fun loadAllByIds(teamIds: IntArray): LiveData<List<Team>>

    @Insert
    fun insertAll(vararg gifts: Team)

    @Delete
    fun delete(team: Team)

    @Query("DELETE FROM team_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(team: Team)

    @Transaction
    @Query("SELECT * FROM team_table")
    fun getTeamWithMembers(): LiveData<List<TeamWithMembers>>
}