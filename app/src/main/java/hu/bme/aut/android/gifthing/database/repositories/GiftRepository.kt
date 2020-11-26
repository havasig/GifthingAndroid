package hu.bme.aut.android.gifthing.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.snakydesign.livedataextensions.emptyLiveData
import hu.bme.aut.android.gifthing.database.AppDatabase
import hu.bme.aut.android.gifthing.database.dao.GiftDao
import hu.bme.aut.android.gifthing.database.models.dto.GiftResponse
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import hu.bme.aut.android.gifthing.database.models.entities.GiftWithOwner
import hu.bme.aut.android.gifthing.services.GiftService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.thread


class GiftRepository @Inject constructor(
    val application: Application
    //private val userRepository: UserRepository
    //TODO: private val giftCache: GiftCache
) {
    private val giftService: GiftService = ServiceBuilder.buildService(GiftService::class.java)
    private val mGiftDao: GiftDao = AppDatabase.getDatabase(application).giftDao()

    companion object {
        val FRESH_TIMEOUT = TimeUnit.MINUTES.toMillis(1) //TODO: set to 10 mins
    }


    fun getByIdWithOwner(giftId: Long): LiveData<GiftWithOwner> {
        refreshGift(giftId)
        return mGiftDao.getByIdWithOwner(giftId)
    }

    fun create(gift: Gift) {
        thread {
            try {
                val response = giftService.create(gift.toServerGift()).execute()
                if (response.isSuccessful) {
                    val createdGift = response.body()!!.toClientGift()
                    mGiftDao.insert(createdGift)
                } else {
                    throw Exception("create " + response.code())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun delete(giftId: Long): LiveData<Boolean> {
        val success = emptyLiveData<Boolean>()
        thread {
            try {
                val serverGiftId = mGiftDao.getServerId(giftId)
                val response = giftService.deleteById(serverGiftId).execute()
                if (response.isSuccessful) {
                    mGiftDao.delete(giftId)
                    success.postValue(true)
                } else {
                    success.postValue(false)
                    throw Exception("delete " + response.code())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return success
    }

    fun reserve(gift: Gift): LiveData<Boolean> {
        val success = emptyLiveData<Boolean>()
        thread {
            try {
                val serverGiftId = mGiftDao.getServerId(gift.giftClientId)
                val response = giftService.reserve(serverGiftId).execute()
                if (response.isSuccessful) {
                    val result = response.body()!!.toClientGift()
                    result.giftClientId = gift.giftClientId
                    mGiftDao.update(result)
                    success.postValue(true)
                } else {
                    success.postValue(false)
                    throw Exception("reserve " + response.code())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return success
    }

    fun release(gift: Gift): LiveData<Boolean> {
        val success = emptyLiveData<Boolean>()
        thread {
            try {
                val serverGiftId = mGiftDao.getServerId(gift.giftClientId)
                val response = giftService.release(serverGiftId).execute()
                if (response.isSuccessful) {
                        val result = response.body()!!.toClientGift()
                        result.giftClientId = gift.giftClientId
                        mGiftDao.update(result)
                        success.postValue(true)
                    } else {
                        success.postValue(false)
                        throw Exception("release " + response.code())
                    }
                } catch (e: Exception) {
                success.postValue(false)
                e.printStackTrace()
            }
        }
        return success
    }

    fun getById(giftId: Long): LiveData<Gift> {
        refreshGift(giftId)
        return mGiftDao.getById(giftId)
    }

    private fun toGift(response: Response<hu.bme.aut.android.gifthing.database.models.server.Gift>): Gift {
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
            try {
                val lastFetch = mGiftDao.getLastFetch(giftId)
                if (lastFetch + FRESH_TIMEOUT < System.currentTimeMillis()) {
                    val serverGiftId = mGiftDao.getServerId(giftId)
                    val response = giftService.getById(serverGiftId).execute()
                    if (response.isSuccessful) {
                        val result = response.body()!!.toClientGift()
                        result.giftClientId = giftId
                        mGiftDao.update(result)
                    } else {
                        throw Exception("refreshGift " + response.code())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshGiftList(giftList: MutableList<GiftResponse>) {
        for (gift in giftList) {
            val current = mGiftDao.getByServerId(gift.id)
            if (current == null) {
                mGiftDao.insert(gift.toClientGift())
            } else {
                val newGift = gift.toClientGift()
                newGift.giftClientId = current.giftClientId
                mGiftDao.update(newGift)
            }
            gift.reservedBy?.let {
                val userRepository = UserRepository(application)
                val currentUser = userRepository.mUserDao.getByServerIdNoLiveData(it.id)
                if (currentUser == null) {
                    userRepository.mUserDao.insert(it.toClientUser())
                } else {
                    val newUser = it.toClientUser()
                    newUser.userClientId = currentUser.userClientId
                    userRepository.mUserDao.update(newUser)
                }
            }
        }
    }
}