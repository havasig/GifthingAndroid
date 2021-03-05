package hu.bme.aut.android.gifthing.database.models.dto

import hu.bme.aut.android.gifthing.database.models.entities.User

class UserResponse(
    var email: String,
    var id: Long,
    var firstName: String? = null,
    var lastName: String? = null,
    var username: String,
    var lastUpdate: Long,
    var gifts: MutableList<GiftResponse>,
    var reservedGifts: MutableList<GiftResponse>,
    var myOwnedTeams: MutableList<TeamUserResponse>,
    var myTeams: MutableList<TeamUserResponse>
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