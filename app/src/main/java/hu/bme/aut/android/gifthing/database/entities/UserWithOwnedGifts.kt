package hu.bme.aut.android.gifthing.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithOwnedGifts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "owner"
    )
    val ownedGifts: List<Gift>
)