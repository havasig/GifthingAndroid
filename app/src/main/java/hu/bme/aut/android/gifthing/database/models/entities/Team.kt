package hu.bme.aut.android.gifthing.database.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "team_table")
data class Team(
    @ColumnInfo(name = "admin_id") val adminId: Long,
    val name: String,
    @ColumnInfo(name = "team_server_id") val teamServerId: Long? = null,
    @ColumnInfo(name = "last_update") var lastUpdate: Long,
    @ColumnInfo(name = "last_fetch") var lastFetch: Long?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "team_client_id") var teamClientId: Long = 0L
}