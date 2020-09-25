package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.security.LoginRequest
import hu.bme.aut.android.gifthing.security.LoginResponse
import retrofit2.http.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}