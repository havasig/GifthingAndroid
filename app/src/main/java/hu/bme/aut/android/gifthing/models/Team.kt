package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName

class Team {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("admin")
    private var admin: User? = null

    @SerializedName("members")
    var members = mutableListOf<User>()
}