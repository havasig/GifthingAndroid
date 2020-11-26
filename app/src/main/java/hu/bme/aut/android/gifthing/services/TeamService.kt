package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.database.models.dto.TeamRequest
import hu.bme.aut.android.gifthing.database.models.dto.TeamResponse
import hu.bme.aut.android.gifthing.database.models.server.Team
import retrofit2.Call
import retrofit2.http.*

interface TeamService {
    @GET("team/{id}")
    @Throws(Exception::class)
    fun getById(@Path("id") id: Long): Call<TeamResponse>

    @GET("team/my-teams")
    fun getMyTeams(): Call<MutableList<TeamResponse>>

    @DELETE("team/delete/{id}")
    suspend fun deleteById(@Path("id") id: Long): Boolean

    @POST("team/create")
    fun create(@Body newTeam: TeamRequest): Call<TeamResponse>
}