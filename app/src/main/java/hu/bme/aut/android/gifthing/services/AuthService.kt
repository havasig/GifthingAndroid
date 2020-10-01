package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.authentication.dto.LoginRequest
import hu.bme.aut.android.gifthing.authentication.dto.LoginResponse
import hu.bme.aut.android.gifthing.authentication.dto.SignupRequest
import hu.bme.aut.android.gifthing.authentication.dto.SignupResponse
import retrofit2.Call
import retrofit2.http.*
import java.lang.Exception

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/signup")
    @Throws(Exception::class)
    fun signup(@Body createUser: SignupRequest): Call<SignupResponse>

    //TODO: logout
}