package hu.bme.aut.android.gifthing.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithOwnedTeams(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "adminId"
    )
    val ownedTeams: List<Team>
)