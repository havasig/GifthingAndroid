package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.dao.UserDao
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.GiftWithOwner
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.services.GiftService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.thread


class GiftRepository @Inject constructor(
    val application: Application
    //TODO: private val giftCache: GiftCache
) {
    private val giftService: GiftService = ServiceBuilder.buildService(GiftService::class.java)
    private val mGiftDao: GiftDao = AppDatabase.getDatabase(application).giftDao()

    companion object {
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(10)
    }


    fun getByIdWithOwner(giftId: Long): LiveData<GiftWithOwner> {
        refreshGift(giftId)
        val userRepository = UserRepository(application)
        userRepository.refreshUser(giftId)
        return mGiftDao.getByIdWithOwner(giftId)
    }

    fun create(gift: Gift) {
        giftService.create(gift.toServerGift())
            .enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.Gift> {
                override fun onResponse(
                    call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                    response: Response<hu.bme.aut.android.gifthing.database.models.Gift>
                ) {
                    val result = toGift(response)
                    AppDatabase.databaseWriteExecutor.execute { mGiftDao.insert(result) }
                }

                override fun onFailure(
                    call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                    t: Throwable
                ) {
                    //TODO: need to save to server
                    AppDatabase.databaseWriteExecutor.execute { mGiftDao.insert(gift) }
                }
            })
    }

    fun delete(giftId: Long) {
        giftService.deleteById(giftId)
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    //be happy, the server deleted/not found/(unauthorized how???) the gift
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    //TODO: need to delete from server later, connection failed
                    //scheduler and isDeleted ?
                }
            })

        AppDatabase.databaseWriteExecutor.execute { mGiftDao.delete(giftId) }
    }

    //TODO: test !!!
    fun reserve(gift: Gift) {
        Thread(Runnable{
            val serverGiftId = mGiftDao.getServerId(gift.giftClientId)
            giftService.reserve(serverGiftId)
                .enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.Gift> {
                    override fun onResponse(
                        call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                        response: Response<hu.bme.aut.android.gifthing.database.models.Gift>
                    ) {
                        val result = toGift(response)
                        result.giftClientId = gift.giftClientId
                        AppDatabase.databaseWriteExecutor.execute { mGiftDao.update(result) }
                    }

                    override fun onFailure(
                        call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                        t: Throwable
                    ) {
                        //TODO: reserve not working without internet. Notify user
                    }
                })
        }).start()
    }

    //TODO: test !!!
    fun release(gift: Gift) {
        Thread(Runnable{
            val serverGiftId = mGiftDao.getServerId(gift.giftClientId)
            giftService.reserve(serverGiftId)
                .enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.Gift> {
                    override fun onResponse(
                        call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                        response: Response<hu.bme.aut.android.gifthing.database.models.Gift>
                    ) {
                        val result = toGift(response)
                        result.giftClientId = gift.giftClientId
                        AppDatabase.databaseWriteExecutor.execute { mGiftDao.update(result) }
                    }

                    override fun onFailure(
                        call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                        t: Throwable
                    ) {
                        //TODO: release not working without internet. Notify user
                    }
                })
        }).start()
    }

    fun getById(giftId: Long): LiveData<Gift> {
        refreshGift(giftId)
        return mGiftDao.getById(giftId)
    }

    private fun toGift(response: Response<hu.bme.aut.android.gifthing.database.models.Gift>): Gift {
        return Gift(
            giftServerId = response.body()!!.id,
            owner = response.body()!!.owner!!,
            name = response.body()!!.name!!,
            description = response.body()!!.description,
            link = response.body()!!.link,
            reservedBy = response.body()!!.reservedBy,
            price = response.body()!!.price,
            lastUpdate = response.body()!!.lastUpdate!!,
            lastFetch = System.currentTimeMillis()
        )
    }

    private fun refreshGift(giftId: Long) {
        thread {
            val lastFetch = mGiftDao.getLastFetch(giftId)
            if (lastFetch + FRESH_TIMEOUT < System.currentTimeMillis()) {
                val serverGiftId = mGiftDao.getServerId(giftId)
                giftService.getById(serverGiftId)
                    .enqueue(object : Callback<hu.bme.aut.android.gifthing.database.models.Gift> {
                        override fun onResponse(
                            call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                            response: Response<hu.bme.aut.android.gifthing.database.models.Gift>
                        ) {
                            if (response.isSuccessful) {
                                val result = toGift(response)
                                result.giftClientId = giftId
                                AppDatabase.databaseWriteExecutor.execute { mGiftDao.update(result) }
                            }
                        }

                        override fun onFailure(
                            call: Call<hu.bme.aut.android.gifthing.database.models.Gift>,
                            t: Throwable
                        ) {
                            //TODO() do nothing? return from old data
                        }
                    })
            }
        }
    }

    fun refreshGiftList(giftList: MutableList<hu.bme.aut.android.gifthing.database.models.Gift>) {
        for(gift in giftList) {
            val clientId = mGiftDao.getClientId(gift.id!!) //TODO: maybe null then insert gift not update
            val clientGift = gift.toClientGift()
            clientGift.giftClientId = clientId
            AppDatabase.databaseWriteExecutor.execute { mGiftDao.update(clientGift) }
        }
    }
}