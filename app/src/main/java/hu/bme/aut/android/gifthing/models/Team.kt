package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Team: Serializable {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("adminId")
    var adminId: Long? = null

    @SerializedName("members")
    var members = mutableListOf<User>()
}