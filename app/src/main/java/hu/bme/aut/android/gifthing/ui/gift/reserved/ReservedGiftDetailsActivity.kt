package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.annotation.SuppressLint
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
import retrofit2.HttpException

class ReservedGiftDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    @SuppressLint("SetTextI18n")
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


                val owner = try {
                    getUser(currentGift.owner!!)
                } catch (e: HttpException) {
                    if(e.code() != 404) {
                        val intent = Intent(this@ReservedGiftDetailsActivity, ErrorActivity::class.java).apply {
                            putExtra("ERROR_MESSAGE", "Hiba van, de nem 404")
                        }
                        startActivity(intent)
                    }
                    null
                }
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
                    putExtra("ERROR_MESSAGE", "Current gift id is null")
                }
                startActivity(intent)
            }
        }
    }
    private suspend fun getGift(id: Long) : Gift? {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.getById(id)
    }

    private suspend fun getUser(id: Long) : User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }
}