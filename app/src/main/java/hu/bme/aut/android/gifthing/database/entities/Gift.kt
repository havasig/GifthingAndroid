package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "gift_table")
data class Gift(
    @ColumnInfo(name = "owner") val owner: Long,
    var name: String,
    var description: String? = null,
    var link: String? = null,
    var reservedBy: Long? = null,
    var price: Int? = null,
    var giftServerId: Long? = null
) {
    @PrimaryKey(autoGenerate = true)
    var giftClientId: Long = 0L
}