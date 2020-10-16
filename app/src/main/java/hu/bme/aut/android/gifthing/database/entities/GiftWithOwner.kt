package hu.bme.aut.android.gifthing.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class GiftWithOwner(
    @Embedded val gift: Gift,
    @Relation(
        parentColumn = "owner",
        entityColumn = "userId"
    )
    val owner: User
)