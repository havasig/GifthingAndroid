package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.authentication.dto.SignupRequest
import hu.bme.aut.android.gifthing.authentication.dto.SignupResponse
import hu.bme.aut.android.gifthing.models.User
import retrofit2.Call
import retrofit2.http.*
import java.lang.Exception

interface UserService {
    @GET("user/{id}")
    @Throws(Exception::class)
    suspend fun findById(@Path("id") id: Long): User

    @GET("user/email/{email}")
    @Throws(Exception::class)
    suspend fun findByEmail(@Path("email") email: String): User



    //TODO: @PUT("user/update") suspend fun update(@Body user: User): User, {update, all, delete}
}