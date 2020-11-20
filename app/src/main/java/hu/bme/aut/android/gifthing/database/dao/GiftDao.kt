package hu.bme.aut.android.gifthing.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import hu.bme.aut.android.gifthing.database.models.entities.GiftWithOwner


@Dao
interface GiftDao {
    @Query("SELECT * FROM gift_table")
    fun getAllGifts(): List<Gift>

    @Query("SELECT * FROM gift_table WHERE gift_client_id IN (:giftIds)")
    fun getAllByIds(giftIds: LongArray): LiveData<List<Gift>>

    @Query("SELECT * FROM gift_table")
    fun getAllForInsert(): List<Gift>

    @Query("SELECT * FROM gift_table WHERE gift_client_id IN (:giftId)")
    fun getById(giftId: Long): LiveData<Gift>

    @Query("SELECT gift_table.gift_server_id FROM gift_table WHERE gift_client_id IN (:giftId)")
    fun getServerId(giftId: Long): Long

    @Query("SELECT gift_table.gift_client_id FROM gift_table WHERE gift_server_id IN (:giftId)")
    fun getClientId(giftId: Long): LiveData<Long>

    @Query("SELECT * FROM gift_table WHERE gift_client_id IN (:giftId)")
    fun getByIdWithOwner(giftId: Long): LiveData<GiftWithOwner>

    @Insert
    fun insertAll(vararg gifts: Gift)

    @Query("DELETE FROM gift_table WHERE gift_client_id = :giftId")
    fun delete(giftId: Long)

    @Update
    fun update(gift: Gift)

    @Query("DELETE FROM gift_table")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(gift: Gift)

    @Query("SELECT gift_table.last_fetch FROM gift_table WHERE gift_client_id == :giftId")
    fun getLastFetch(giftId: Long): Long
}