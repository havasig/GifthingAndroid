package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName
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

    @SerializedName("password")
    var password: String? = null

    @SerializedName("gifts")
    var gifts = mutableListOf<Gift>()

    @SerializedName("reservedGifts")
    var reservedGifts = mutableListOf<Gift>()

    @SerializedName("myOwnedTeams")
    var myOwnedTeams = mutableListOf<Team>()

    @SerializedName("myTeams")
    var myTeams = mutableListOf<Team>()
}