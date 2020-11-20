package hu.bme.aut.android.gifthing.database.models.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithOwnedGifts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_client_id",
        entityColumn = "owner"
    )
    val ownedGifts: List<Gift>
)