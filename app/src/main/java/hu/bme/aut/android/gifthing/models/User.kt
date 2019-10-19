package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("name")
    var name: String? = null
    @SerializedName("password")
    var password: String? = null

    //TODO? getters and setters
}