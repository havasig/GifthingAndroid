package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.models.User
import retrofit2.http.*
import java.lang.Exception

interface UserService {
    @GET("user/{id}")
    @Throws(Exception::class)
    suspend fun getById(@Path("id") id: Long): User

    @GET("user/email/{email}")
    @Throws(Exception::class)
    suspend fun getByEmail(@Path("email") email: String): User

    @POST("user/create")
    @Throws(Exception::class)
    suspend fun create(@Body createUser: User): User


    //TODO: @PUT("user/update") suspend fun update(@Body user: User): User, {update, all, delete}
}