package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.entities.Gift


@Dao
interface GiftDao {
    @Query("SELECT * FROM gift_table")
    fun getAll(): LiveData<List<Gift>>

    @Query("SELECT * FROM gift_table WHERE giftId IN (:giftIds)")
    fun loadAllByIds(giftIds: IntArray): LiveData<List<Gift>>

    @Insert
    fun insertAll(vararg gifts: Gift)

    @Delete
    fun delete(gift: Gift)

    @Query("DELETE FROM gift_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(gift: Gift)
}