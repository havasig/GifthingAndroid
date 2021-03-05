package hu.bme.aut.android.gifthing.database.models.dto

import hu.bme.aut.android.gifthing.database.models.entities.Gift

class GiftResponse(
	var id: Long,
	var name: String,
	var link: String? = null,
	var description: String? = null,
	var price: Int? = null,
	var owner: GiftUserResponse,
	var reservedBy: GiftUserResponse? = null,
	var lastUpdate: Long? = null
) {
	fun toClientGift(): Gift {
		return Gift(
			giftServerId = this.id,
			owner = this.owner.id,
			name = this.name,
			description = this.description,
			link = this.link,
			reservedBy = this.reservedBy?.id,
			price = this.price,
			lastUpdate = System.currentTimeMillis(),
			lastFetch = System.currentTimeMillis()
		)
	}
}