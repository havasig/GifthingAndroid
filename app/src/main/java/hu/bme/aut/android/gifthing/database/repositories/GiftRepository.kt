package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.GiftWithOwner
import hu.bme.aut.android.gifthing.services.GiftService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class GiftRepository @Inject constructor(
    application: Application
    //TODO: private val giftCache: GiftCache
) {
    private val giftService = ServiceBuilder.buildService(GiftService::class.java)
    private val mGiftDao: GiftDao = AppDatabase.getDatabase(application).giftDao()

    /*
    fun getById(id: Long): LiveData<Gift> {
        return mGiftDao.getById(id.toInt())
    }
     */

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
        /*
        val cached : LiveData<Gift> = giftCache.get(giftId)
        if (cached != null) {
            return cached
        }
         */
        val data = MutableLiveData<Gift>()


        //giftCache.put(giftId, data)

        giftService.getById(giftId).enqueue(object : Callback<Gift> {
            override fun onResponse(call: Call<Gift>, response: Response<Gift>) {
                data.value = response.body()
            }

            // Error case is left out for brevity.
            override fun onFailure(call: Call<Gift>, t: Throwable) {
                TODO()
            }
        })
        return data
    }
}