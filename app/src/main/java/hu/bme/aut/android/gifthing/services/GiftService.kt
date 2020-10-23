package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.database.entities.Gift
import retrofit2.Call
import retrofit2.http.*

interface GiftService {
    @GET("gift/{id}")
    fun getById(@Path("id") id: Long): Call<Gift>

    @DELETE("gift/delete/{id}")
    suspend fun deleteById(@Path("id") id: Long): Boolean

    @POST("gift/create")
    suspend fun create(@Body newGift: Gift): Gift

    @PUT("gift/reserve/{giftId}/{userId}")
    suspend fun reserveGift(@Path("giftId") giftId: Long, @Path("userId") userId: Long): Gift

    //TODO: update
}