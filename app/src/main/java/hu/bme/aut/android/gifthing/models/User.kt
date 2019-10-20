package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName

class User(var email: String) {
    @SerializedName("id")
    var id: Long = 0
    @SerializedName("name")
    var name: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("gifts")
    var gifts = mutableListOf<Gift>()
    @SerializedName("reservedGifts")
    var reservedGifts = mutableListOf<Gift>()

    //TODO? getters and setters
}