package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "gift_table")
data class Gift(
    @ColumnInfo(name = "owner") val owner: Long,
    val name: String,
    val description: String? = null,
    val link: String? = null,
    val reservedBy: Long? = null,
    val price: Int? = null
) {
    @PrimaryKey(autoGenerate = true) var giftId: Long = 0L
}