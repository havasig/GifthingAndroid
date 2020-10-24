package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*


@Entity(tableName = "gift_table")
data class Gift(
    @ColumnInfo(name = "owner") val owner: Long,
    var name: String,
    var description: String? = null,
    var link: String? = null,
    var reservedBy: Long? = null,
    var price: Int? = null,
    var giftServerId: Long? = null,
    @ColumnInfo(name = "last_update") var lastUpdate: Long,
    @ColumnInfo(name = "last_fetch") var lastFetch: Long?
) {
    @PrimaryKey(autoGenerate = true)
    var giftClientId: Long = 0L
}