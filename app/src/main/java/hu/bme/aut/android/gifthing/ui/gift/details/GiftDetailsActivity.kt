package hu.bme.aut.android.gifthing.ui.gift.details

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import kotlinx.android.synthetic.main.activity_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


class GiftDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var giftId = 0L
    private lateinit var currentGift: Gift
    private val mGiftViewModel: GiftViewModel by viewModels()
    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_details)

        giftId = intent.getLongExtra("GIFT_ID", 0)

        loadGiftDetails(giftId)

        btnReserve.setOnClickListener {

            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            onReserveOrFree()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure You want to free/reserve this gift?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show()
        }
    }

    private fun loadGiftDetails(giftId: Long) {
        mGiftViewModel.getById(giftId).observe(
            this,
            Observer<Gift> { gift ->
                currentGift = gift
                tvGiftName.text = gift.name
                tvGiftPrice.text = gift.price?.toString() ?: ""
                tvGiftLink.text = gift.link ?: ""
                tvGiftDescription.text = gift.description ?: ""

                gift.reservedBy?.let {
                    if(it == AppPreferences.currentId!!) {
                        btnReserve.text = resources.getString(R.string.free)
                        tvReservedBy.text = getString(R.string.you)
                    } else {
                        loadReservedBy(it)
                    }
                } ?: run {
                    tvReservedBy.text = resources.getString(R.string.currently_free)
                    btnReserve.text = resources.getString(R.string.reserve)
                }
            }
        )
    }

    private fun loadReservedBy(reservedById: Long) {
        btnReserve.visibility = View.GONE
        mUserViewModel.getById(reservedById).observe(
            this,
            Observer<User> { user ->
                try {
                    if (user.firstName != "" || user.lastName != "") {
                        val tempName = "(${user.firstName} ${user.lastName})"
                        tvReservedBy.text = tempName
                    } else {
                        tvReservedBy.text = user.username
                    }
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "Reserved user not found", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun onReserveOrFree() {
        currentGift.reservedBy?.let {
            if(it == AppPreferences.currentId!!) {
                currentGift.reservedBy = null
                mGiftViewModel.reserve(currentGift)
                Toast.makeText(baseContext, "Freed successfully", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            currentGift.reservedBy = AppPreferences.currentId!!
            mGiftViewModel.reserve(currentGift)
            Toast.makeText(baseContext, "Reserved successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
