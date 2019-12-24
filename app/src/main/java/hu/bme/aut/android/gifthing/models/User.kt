package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User(
    var email: String,
    var firstName: String,
    var lastName: String
) : Serializable {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("nickName")
    var nickName: String? = null

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