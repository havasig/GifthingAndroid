package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.GiftWithOwner
import hu.bme.aut.android.gifthing.services.GiftService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class GiftRepository @Inject constructor(
    application: Application
    //TODO: private val giftCache: GiftCache
) {
    companion object {
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(10)
    }

    private val giftService = ServiceBuilder.buildService(GiftService::class.java)
    private val mGiftDao: GiftDao = AppDatabase.getDatabase(application).giftDao()

    fun getByIdWithOwner(id: Long): LiveData<GiftWithOwner> {
        return mGiftDao.getByIdWithOwner(id.toInt())
    }

    fun insert(gift: Gift) {
        AppDatabase.databaseWriteExecutor.execute { mGiftDao.insert(gift) }
    }

    fun delete(gift: Gift) {
        AppDatabase.databaseWriteExecutor.execute { mGiftDao.delete(gift) }
    }

    fun reserve(gift: Gift) {
        AppDatabase.databaseWriteExecutor.execute { mGiftDao.update(gift) }
    }


    fun getById(giftId: Long): LiveData<Gift> {
        refreshGift(giftId)
        return mGiftDao.getById(giftId.toInt())
    }

    private fun refreshGift(giftId: Long) {
        val lastFetch = true//TODO: mGiftDao.getLastFetch(giftId.toInt())
        if (lastFetch) {
            // Refreshes the data.
            //TODO: add real giftid
            giftService.getById(1).enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.Gift> {
                override fun onResponse(call: Call<hu.bme.aut.android.gifthing.database.models.Gift>, response: Response<hu.bme.aut.android.gifthing.database.models.Gift>) {
                    val serverGift = Gift(
                        giftServerId = response.body()!!.id,
                        owner = response.body()!!.owner!!,
                        name = response.body()!!.name!!,
                        description= response.body()!!.description,
                        link = response.body()!!.link,
                        reservedBy = response.body()!!.reservedBy,
                        price = response.body()!!.price,
                        lastUpdate = response.body()!!.lastUpdate!!,
                        lastFetch = System.currentTimeMillis()
                    )
                    serverGift.giftClientId = 1 //TODO: set clientID
                    AppDatabase.databaseWriteExecutor.execute {
                        mGiftDao.update(serverGift)
                    }
                }

                // Error case is left out for brevity.
                override fun onFailure(call: Call<hu.bme.aut.android.gifthing.database.models.Gift>, t: Throwable) {
                    //TODO() do nothing? return from old data
                }
            })
        }
    }
}