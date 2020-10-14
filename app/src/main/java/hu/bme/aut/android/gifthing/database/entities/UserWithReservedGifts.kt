package hu.bme.aut.android.gifthing.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithReservedGifts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "reservedBy"
    )
    val reservedGifts: List<Gift>
)