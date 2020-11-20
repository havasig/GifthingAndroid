package hu.bme.aut.android.gifthing.database.models.server

import com.google.gson.annotations.SerializedName
import hu.bme.aut.android.gifthing.database.models.entities.User
import java.io.Serializable

class User(
    var username: String,
    var email: String
) : Serializable {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("gifts")
    var gifts = mutableListOf<Gift>()

    @SerializedName("reservedGifts")
    var reservedGifts = mutableListOf<Gift>()

    @SerializedName("myOwnedTeams")
    var myOwnedTeams = mutableListOf<Team>()

    @SerializedName("myTeams")
    var myTeams = mutableListOf<Team>()

    @SerializedName("lastUpdate")
    var lastUpdate: Long? = null

    fun toClientUser(): User {
        return User(
            email = this.email,
            username = this.username,
            firstName = this.firstName,
            lastName = this.lastName,
            userServerId = this.id,
            lastUpdate = this.lastUpdate!!,
            lastFetch = System.currentTimeMillis()
        )
    }
}