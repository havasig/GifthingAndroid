package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.GiftService
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import kotlinx.android.synthetic.main.activity_reserved_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReservedGiftDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserved_gift_details)

        val giftId = intent.getLongExtra("GIFT_ID", 0)

        launch {
            val currentGift = getGift(giftId)
            if (currentGift != null) {
                tvGiftName.text = currentGift.name
                tvGiftPrice.text = currentGift.price.toString()
                tvGiftLink.text = currentGift.link
                tvGiftDescription.text = currentGift.description


                val owner = getUser(currentGift.owner!!)
                if(owner != null) {
                    tvOwnerName.text = owner.firstName + " " + owner.lastName
                } else {
                    val intent = Intent(this@ReservedGiftDetailsActivity, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Nincs tuljadonosa az ajándéknak :O")
                    }
                    startActivity(intent)
                }
            } else {
                val intent = Intent(this@ReservedGiftDetailsActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Na itt valami komoly baj van (0 id gift-et akart elkérni)")
                }
                startActivity(intent)
            }
        }
    }
    private suspend fun getGift(id: Long) : Gift? {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.getById(id)
    }

    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }
}