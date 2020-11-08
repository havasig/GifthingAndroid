package hu.bme.aut.android.gifthing.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithOwnedTeams(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_client_id",
        entityColumn = "admin_id"
    )
    val ownedTeams: List<Team>
)