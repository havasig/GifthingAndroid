package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.entities.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * FROM user_table")
    fun getAllForInsert(): List<User>

    @Query("SELECT username FROM user_table")
    fun getAllUsername(): LiveData<List<String>>

    @Query("SELECT * FROM user_table WHERE user_id IN (:userId)")
    fun getCurrentUser(userId: Long): LiveData<User>

    @Query("SELECT * FROM user_table WHERE user_id IN (:userIds)")
    fun getAllByIds(userIds: LongArray): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE user_id = :userId LIMIT 1")
    fun getById(userId: Long): LiveData<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_id IN (:userId)")
    fun getUserWithReservedGifts(userId: Long): LiveData<UserWithReservedGifts>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_id IN (:userId)")
    fun getUserWithOwnedGifts(userId: Long): LiveData<UserWithOwnedGifts>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_id IN (:userId)")
    fun getMeWithOwnedGifts(userId: Long): LiveData<UserWithOwnedGifts>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_id IN (:userId)")
    fun getUserWithOwnedTeams(userId: Long): LiveData<UserWithOwnedTeams>

    @Transaction
    @Query("SELECT * FROM user_table WHERE user_id IN (:userId)")
    fun getUserWithTeams(userId: Long): LiveData<UserWithTeams>
}