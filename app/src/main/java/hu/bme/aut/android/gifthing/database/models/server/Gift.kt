package hu.bme.aut.android.gifthing.database.models.server

import com.google.gson.annotations.SerializedName
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import java.io.Serializable

class Gift : Serializable {
    @SerializedName("id")
    var id: Long? = null

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

    @SerializedName("lastUpdate")
    var lastUpdate: Long? = null


    fun toClientGift(): Gift {
        return Gift(
            giftServerId = this.id,
            owner = this.owner!!,
            name = this.name!!,
            description = this.description,
            link = this.link,
            reservedBy = this.reservedBy,
            price = this.price,
            lastUpdate = System.currentTimeMillis(),
            lastFetch = System.currentTimeMillis()
        )
    }
}