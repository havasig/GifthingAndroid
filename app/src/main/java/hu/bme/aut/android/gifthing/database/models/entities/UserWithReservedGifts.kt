package hu.bme.aut.android.gifthing.database.models.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithReservedGifts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_client_id",
        entityColumn = "reserved_by"
    )
    val reservedGifts: List<Gift>
)