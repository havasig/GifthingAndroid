package hu.bme.aut.android.gifthing.ui.gift

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import hu.bme.aut.android.gifthing.Services.GiftService
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.android.synthetic.main.dialog_create_gift.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


class CreateGiftActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(hu.bme.aut.android.gifthing.R.layout.dialog_create_gift)
        setFinishOnTouchOutside(false)

        btnCreate.setOnClickListener {
            if (etGiftName.text.toString() == "") {
                val intent = Intent(this, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Name is required")
                }
                startActivity(intent)
                return@setOnClickListener
            }
            var link: String? = null
            if (etGiftLink.text.toString() != "") {
                link = etGiftLink.text.toString()
                /*TODO: link is valid or not
                if (!URLUtil.isValidUrl(etGiftLink.text.toString())) {
                    val intent = Intent(this, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Add a valid link (or leave it empty)")
                    }
                    startActivity(intent)
                    return@setOnClickListener
                } else {
                    link = etGiftLink.text.toString()
                }*/
            }

            val newGift = Gift()

            newGift.name = etGiftName.text.toString()
            if (etGiftPrice.text.toString() != "") {
                newGift.price = Integer.parseInt(etGiftPrice.text.toString())
            }

            if (etGiftDescription.text.toString() != "") {
                newGift.description = etGiftDescription.text.toString()
            }

            if (link != null) {
                newGift.link = link
            }

            launch {
                val currentUserId = HomeActivity.CURRENT_USER_ID
                newGift.owner = currentUserId
                val savedGift = createGift(newGift)

                val result = Intent().apply {
                    putExtra("GIFT", savedGift)
                }
                setResult(Activity.RESULT_OK, result)

                finish()
            }
        }

        btnCancel.setOnClickListener{
            super.onBackPressed()
        }
    }

    private suspend fun createGift(newGift: Gift) : Gift {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.create(newGift)
    }
}
