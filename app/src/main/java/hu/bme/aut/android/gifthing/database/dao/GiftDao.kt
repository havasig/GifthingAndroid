package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.GiftWithOwner


@Dao
interface GiftDao {
    @Query("SELECT * FROM gift_table")
    fun getAll(): LiveData<List<Gift>>

    @Query("SELECT * FROM gift_table WHERE giftId IN (:giftIds)")
    fun getAllByIds(giftIds: IntArray): LiveData<List<Gift>>

    @Query("SELECT * FROM gift_table WHERE giftId IN (:giftId)")
    fun getById(giftId: Int): LiveData<Gift>

    @Query("SELECT * FROM gift_table WHERE giftId IN (:giftId)")
    fun getByIdWithOwner(giftId: Int): LiveData<GiftWithOwner>

    @Insert
    fun insertAll(vararg gifts: Gift)

    @Delete
    fun delete(gift: Gift)

    @Update
    fun update(gift: Gift)

    @Query("DELETE FROM gift_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(gift: Gift)
}