package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.entities.*


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
    fun insert(team: Team): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWithMembers(team: Team, members: List<User>) //TODO: ??? ez így jó?

    @Transaction
    @Query("SELECT * FROM team_table WHERE teamId IN (:teamId)")
    fun getTeamWithMembers(teamId: Long): LiveData<TeamWithMembers>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUserTeamCross(userTeamCrossRef: UserTeamCrossRef)
}