package hu.bme.aut.android.gifthing.Services

import hu.bme.aut.android.gifthing.models.Team
import retrofit2.http.*

interface TeamService {
    @GET("team/{id}")
    suspend fun getById(@Path("id") id: Long): Team

    @DELETE("team/delete/{id}")
    suspend fun deleteById(@Path("id") id: Long): Boolean

    @POST("team/create")
    suspend fun create(@Body newTeam: Team): Team
}