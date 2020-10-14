package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.entities.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE userId IN (:userIds)")
    fun getAllByIds(userIds: LongArray): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE userId = :userId LIMIT 1")
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
    @Query("SELECT * FROM user_table")
    fun getUserWithReservedGifts(): LiveData<List<UserWithReservedGifts>>

    @Transaction
    @Query("SELECT * FROM user_table")
    fun getUserWithOwnedGifts(): LiveData<List<UserWithOwnedGifts>>
    @Transaction
    @Query("SELECT * FROM user_table WHERE userId IN (:userId)")
    fun getMeWithOwnedGifts(userId: Long): LiveData<UserWithOwnedGifts>

    @Transaction
    @Query("SELECT * FROM user_table")
    fun getUserWithOwnedTeams(): List<UserWithOwnedTeams>

    @Transaction
    @Query("SELECT * FROM user_table")
    fun getUserWithTeams(): LiveData<List<UserWithTeams>>
}