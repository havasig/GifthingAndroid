package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "team_table")
data class Team(
    @ColumnInfo(name = "admin_id") val adminId: Long,
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "team_id") var teamId: Long = 0L
}