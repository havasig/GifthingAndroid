package hu.bme.aut.android.gifthing.models

import com.google.gson.annotations.SerializedName

class User(var email: String) {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("nickName")
    var nickName: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("gifts")
    var gifts = mutableListOf<Gift>()

    @SerializedName("reservedGifts")
    var reservedGifts = mutableListOf<Gift>()

    @SerializedName("myOwnedGroups")
    var myOwnedGroups = mutableListOf<Group>()

    @SerializedName("myGroups")
    var myGroups = mutableListOf<Group>()
}