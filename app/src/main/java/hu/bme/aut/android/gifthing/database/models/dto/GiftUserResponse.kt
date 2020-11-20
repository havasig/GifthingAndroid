package hu.bme.aut.android.gifthing.database.models.dto

class GiftUserResponse(
	var name: String,
	var id: Long,
	var link: String? = null,
	var description: String? = null,
	var price: Int? = null,
	var owner: Long
)