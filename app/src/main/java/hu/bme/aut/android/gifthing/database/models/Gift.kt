package hu.bme.aut.android.gifthing.database.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Gift : Serializable {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("link")
    var link: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("price")
    var price: Int? = null

    @SerializedName("owner")
    var owner: Long? = null

    @SerializedName("reservedBy")
    var reservedBy: Long? = null
}