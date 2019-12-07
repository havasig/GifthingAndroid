package hu.bme.aut.android.gifthing.ui.gift.details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.Services.GiftService
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.models.Gift
import kotlinx.android.synthetic.main.activity_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.home.HomeActivity


class GiftToReserveDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var giftId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_details)

        giftId = intent.getLongExtra("GIFT_ID", 0)

        loadGiftDetails()


        btnReserve.setOnClickListener{
            val tmp = onReserve(giftId)
            //TODO: (az ajándék nem érkezik vissza mire a ui-t már be akarná tölteni újra)
            loadGiftDetails()
        }
    }

    private fun loadGiftDetails() {
        launch {
            val currentGift: Gift? = getGift(giftId)

            if (currentGift != null) {
                tvGiftName.text = currentGift.name
                tvGiftPrice.text = currentGift.price.toString()
                tvGiftLink.text = currentGift.link
                tvGiftDescription.text = currentGift.description

                currentGift.reservedBy?.let {

                    btnReserve.visibility = View.GONE

                    val reserveUser = getUser(it)
                    reserveUser?.let {
                        if(reserveUser.name != null){
                            tvReservedBy.text = reserveUser.name
                        }
                        else { tvReservedBy.text = reserveUser.email }
                    }

                } ?: let { tvReservedBy.text = resources.getString(R.string.currently_free) }

                //TODO: reserve gift should work with this added:
                /*if(currentGift.reservedBy == HomeActivity.CURRENT_USER_ID)
                    btnReserve.text = resources.getString(R.string.unreserve)
                else {
                    btnReserve.text = resources.getString(R.string.reserve)
                }*/

            } else {
                val intent = Intent(this@GiftToReserveDetailsActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Na itt valami komoly baj van (0 id gift-et akart elkérni)")
                }
                startActivity(intent)
            }
        }
    }

    private fun onReserve(giftId: Long): Gift? {
        var reservedGift: Gift? = null
        launch {
            reservedGift = reserveGift(giftId, HomeActivity.CURRENT_USER_ID)
            if (reservedGift != null) {
                if(reservedGift!!.reservedBy == HomeActivity.CURRENT_USER_ID) {
                    Toast.makeText(baseContext, "Reserved successfully", Toast.LENGTH_SHORT).show()
                    //Snackbar.make(gift_constraint_layout, "Reserved successfully", Snackbar.LENGTH_LONG).show()
                    btnReserve.text = resources.getString(R.string.reserve)
                } else if(reservedGift!!.reservedBy == null) {
                    Toast.makeText(baseContext, "Unreserved successfully", Toast.LENGTH_SHORT).show()
                    //Snackbar.make(gift_constraint_layout, "Unreserved successfully", Snackbar.LENGTH_LONG).show()
                    btnReserve.text = resources.getString(R.string.unreserve)
                } else {
                    Toast.makeText(baseContext, "Reserved by someone else", Toast.LENGTH_SHORT).show()
                    //Snackbar.make(gift_constraint_layout, "Reserved by someone else", Snackbar.LENGTH_LONG).show()
                }
            } else {
                val intent = Intent(this@GiftToReserveDetailsActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "reserved gift is null wtf")
                }
                startActivity(intent)
            }
        }
        return reservedGift
    }

    private suspend fun getGift(id: Long) : Gift? {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.getById(id)
    }
    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }

    private suspend fun reserveGift(giftId: Long, userId: Long) : Gift? {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.reserveGift(giftId, userId)
    }
}
