package hu.bme.aut.android.gifthing.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.bme.aut.android.gifthing.database.models.User

@Entity(tableName = "user_table")
data class User(
    val email: String,
    val username: String,
    @ColumnInfo(name = "first_name") val firstName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L

    fun toServerUser(): User {
        val user = User(
            email = this.email,
            username = this.username
        )
        user.firstName = this.firstName
        user.lastName = this.lastName
        user.gifts = mutableListOf()
        user.reservedGifts = mutableListOf()
        user.myOwnedTeams = mutableListOf()
        user.myTeams = mutableListOf()

        return user
    }
}