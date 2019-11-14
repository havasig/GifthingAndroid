package hu.bme.aut.android.gifthing.Services

import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import retrofit2.http.*
import java.util.*

interface UserService {
    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: Long): User?

    @GET("user/email/{email}")
    suspend fun getUserByEmail(@Path("email") id: String): User?

    @POST("user/create")
    suspend fun createUser(@Body createUser: User): Boolean

    @PUT("user/update")
    suspend fun updateUser(@Body user: User): Boolean
}