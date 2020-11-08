package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import hu.bme.aut.android.gifthing.database.models.User


@Entity(tableName = "user_table", indices = [Index(value = ["username"], unique = true)])
data class User(
    val email: String,
    val username: String,
    @ColumnInfo(name = "first_name") val firstName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String? = null,
    @ColumnInfo(name = "user_server_id") var userServerId: Long? = null,
    @ColumnInfo(name = "last_update") var lastUpdate: Long,
    @ColumnInfo(name = "last_fetch") var lastFetch: Long?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_client_id") var userClientId: Long = 0L
}