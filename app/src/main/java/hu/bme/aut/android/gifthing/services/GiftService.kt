package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.database.models.Gift
import retrofit2.Call
import retrofit2.http.*

interface GiftService {
    @GET("gift/{id}")
    fun getById(@Path("id") id: Long): Call<Gift>

    @DELETE("gift/delete/{id}")
    fun deleteById(@Path("id") id: Long): Call<Boolean>

    @POST("gift/create")
     fun create(@Body newGift: Gift): Call<Gift>

    @PUT("gift/reserve/{id}")
    fun reserve(@Path("id") id: Long): Call<Gift>

    @PUT("gift/release/{id}")
    fun release(@Path("id") id: Long): Call<Gift>

    @GET("gift/{id}/with-owner")
    fun findByIdWithOwner(@Path("id") id: Long): Call<Gift>

    //TODO: update
}