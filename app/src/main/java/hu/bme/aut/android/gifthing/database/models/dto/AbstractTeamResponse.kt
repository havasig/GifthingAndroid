package hu.bme.aut.android.gifthing.database.models.dto

import hu.bme.aut.android.gifthing.database.models.entities.Team

open class AbstractTeamResponse(
    var name: String,
    var id: Long,
    var adminId: Long,
    var lastUpdate: Long
) {
    fun toClientTeam(): Team {
        return Team(
            adminId = adminId,
            name = this.name,
            teamServerId = this.id,
            lastUpdate = this.lastUpdate,
            lastFetch = System.currentTimeMillis()
        )
    }
}