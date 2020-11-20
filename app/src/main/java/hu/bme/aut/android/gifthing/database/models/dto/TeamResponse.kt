package hu.bme.aut.android.gifthing.database.models.dto

class TeamResponse(
	name: String,
	id: Long,
	adminId: Long,
	lastUpdate: Long,
	var members: MutableList<UserResponse>
): AbstractTeamResponse(name, id, adminId, lastUpdate)