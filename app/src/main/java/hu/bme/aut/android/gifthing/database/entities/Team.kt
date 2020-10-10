package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "team_table")
data class Team(
    val adminId: Long,
    val name: String
) {
    @PrimaryKey(autoGenerate = true) var teamId: Long = 0L
}