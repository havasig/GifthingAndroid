package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.models.entities.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): List<User>

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT username FROM user_table")
    fun getAllUsername(): LiveData<List<String>>

    @Query("SELECT * FROM user_table WHERE user_client_id = :userId LIMIT 1")
    fun getById(userId: Long): LiveData<User>

    @Query("SELECT * FROM user_table WHERE user_server_id = :userId LIMIT 1")
    fun getByServerId(userId: Long): LiveData<User>

    @Query("SELECT * FROM user_table WHERE user_server_id = :userId LIMIT 1")
    fun getByServerIdNoLiveData(userId: Long): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_client_id IN (:userId)")
    fun getUserWithReservedGifts(userId: Long): LiveData<UserWithReservedGifts>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_client_id IN (:userId)")
    fun getUserWithOwnedGifts(userId: Long): LiveData<UserWithOwnedGifts>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_client_id IN (:userId)")
    fun getUserWithOwnedTeams(userId: Long): LiveData<UserWithOwnedTeams>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_client_id IN (:userId)")
    fun getUserWithTeams(userId: Long): LiveData<UserWithTeams>

    @Query("SELECT user_table.last_fetch FROM user_table WHERE user_server_id == :userId")
    fun getLastFetch(userId: Long): Long?

    @Query("SELECT user_server_id FROM user_table WHERE user_client_id IN (:userId)")
    fun getServerId(userId: Long): Long
}