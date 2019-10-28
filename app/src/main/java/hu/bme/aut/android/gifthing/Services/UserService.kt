package hu.bme.aut.android.gifthing.Services

import hu.bme.aut.android.gifthing.models.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int): User
}