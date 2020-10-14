package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.models.Gift
import hu.bme.aut.android.gifthing.database.models.User
import hu.bme.aut.android.gifthing.services.GiftService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import kotlinx.android.synthetic.main.activity_reserved_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ReservedGiftDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserved_gift_details)

        val giftId = intent.getLongExtra("GIFT_ID", 0)

        launch {
            try {
                val currentGift = getGift(giftId)
                tvGiftName.text = currentGift.name
                tvGiftPrice.text = currentGift.price.toString()
                tvGiftLink.text = currentGift.link
                tvGiftDescription.text = currentGift.description

                try {
                    val owner = getUser(currentGift.owner!!)
                    tvOwnerName.text = owner.firstName + " " + owner.lastName
                } catch (e: HttpException) {
                    val intent =
                        Intent(this@ReservedGiftDetailsActivity, ErrorActivity::class.java).apply {
                            putExtra("ERROR_MESSAGE", "Gift owner not found")
                        }
                    startActivity(intent)
                }

            } catch (e: HttpException) {
                val intent =
                    Intent(this@ReservedGiftDetailsActivity, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Current gift id is null")
                    }
                startActivity(intent)
            }
        }
    }

    private suspend fun getGift(id: Long): Gift {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.getById(id)
    }

    private suspend fun getUser(id: Long): User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.findById(id)
    }
}