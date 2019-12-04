package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Team: Serializable {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("admin")
    private var admin: Long? = null

    @SerializedName("members")
    var members = mutableListOf<User>()
}