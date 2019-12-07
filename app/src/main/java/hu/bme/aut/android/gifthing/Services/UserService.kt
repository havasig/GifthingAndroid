package hu.bme.aut.android.gifthing.Services

import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import retrofit2.http.*
import java.util.*

interface UserService {
    @GET("user/{id}")
    suspend fun getById(@Path("id") id: Long): User?

    @GET("user/email/{email}")
    suspend fun getByEmail(@Path("email") email: String): User?

    @POST("user/create")
    suspend fun create(@Body createUser: User): User?

    @PUT("user/update")
    suspend fun update(@Body user: User): Boolean
}