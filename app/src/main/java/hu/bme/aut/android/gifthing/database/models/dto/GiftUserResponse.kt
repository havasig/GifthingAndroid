package hu.bme.aut.android.gifthing.database.models.dto

import hu.bme.aut.android.gifthing.database.models.entities.User

class GiftUserResponse(
    var id: Long,
    var email: String,
    var firstName: String,
    var lastName: String,
    var username: String,
    var lastUpdate: Long
) {
    fun toClientUser(): User {
        return User(
            email = this.email,
            username = this.username,
            firstName = this.firstName,
            lastName = this.lastName,
            userServerId = this.id,
            lastUpdate = this.lastUpdate,
            lastFetch = System.currentTimeMillis()
        )
    }
}