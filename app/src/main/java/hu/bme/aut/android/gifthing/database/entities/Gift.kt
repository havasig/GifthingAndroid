package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import hu.bme.aut.android.gifthing.database.models.Gift
import java.time.LocalDateTime
import java.util.*


@Entity(tableName = "gift_table")
data class Gift(
    @ColumnInfo(name = "owner") val owner: Long,
    var name: String,
    var description: String? = null,
    var link: String? = null,
    @ColumnInfo(name = "reserved_by") var reservedBy: Long? = null,
    var price: Int? = null,
    @ColumnInfo(name = "gift_server_id") var giftServerId: Long? = null,
    @ColumnInfo(name = "last_update") var lastUpdate: Long,
    @ColumnInfo(name = "last_fetch") var lastFetch: Long?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "gift_client_id") var giftClientId: Long = 0L

    fun toServerGift(): Gift {
        return Gift().apply {
            id = this@Gift.giftServerId
            name = this@Gift.name
            link = this@Gift.link
            description = this@Gift.description
            price = this@Gift.price
            owner = this@Gift.owner
            reservedBy = this@Gift.reservedBy
            lastUpdate = this@Gift.lastUpdate
        }
    }
}
