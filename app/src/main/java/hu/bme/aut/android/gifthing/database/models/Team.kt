package hu.bme.aut.android.gifthing.database.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import hu.bme.aut.android.gifthing.database.entities.Team

class Team : Serializable {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("adminId")
    var adminId: Long? = null

    @SerializedName("members")
    var members = mutableListOf<User>()

    fun toClientTeam(): Team {
        return Team(
            adminId = adminId!!,
            name = this.name!!,
            teamServerId = this.id
        )
    }

}