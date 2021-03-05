package hu.bme.aut.android.gifthing.database.models.dto

class TeamUserResponse (
    name: String,
    id: Long,
    adminId: Long,
    lastUpdate: Long,
    var memberIds: MutableList<Long>
): AbstractTeamResponse(name, id, adminId, lastUpdate)