package hu.bme.aut.android.gifthing.database.models.dto

class TeamRequest(
	var name: String,
	var id: Long?,
	var adminId: Long,
	var members: MutableList<Long>
)