package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.database.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("user/{id}")
    @Throws(Exception::class)
    fun getById(@Path("id") id: Long): Call<User>

    @GET("user/email/{email}")
    @Throws(Exception::class)
    suspend fun findByEmail(@Path("email") email: String): User

    @GET("user/username/{username}")
    @Throws(Exception::class)
    suspend fun findByUsername(@Path("username") username: String): User

    @GET("user/usernames")
    @Throws(Exception::class)
    fun getUsername(): Call<ArrayList<String>>

    //TODO: @PUT("user/update") suspend fun update(@Body user: User): User, {update, all, delete}
}