package hu.bme.aut.android.gifthing.ui.gift.my

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import kotlinx.android.synthetic.main.activity_my_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


class MyGiftDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val mGiftViewModel: GiftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_gift_details)

        val giftId = intent.getLongExtra("GIFT_ID", 0)
        var deleted = false
        lateinit var currentGift: Gift
        mGiftViewModel.getById(giftId).observe(
            this,
            Observer<Gift> { gift ->
                if (!deleted) {
                    try {
                        tvGiftName.text = gift.name
                        tvGiftDescription.text = gift.description ?: ""
                        tvGiftPrice.text = gift.price?.toString() ?: ""
                        tvGiftLink.text = gift.link ?: ""
                        currentGift = gift
                    } catch (e: Exception) {
                        Toast.makeText(this, "Gift not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        btnDelete.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            mGiftViewModel.delete(currentGift)
                            deleted = true
                            finish()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show()
        }

        btnEdit.setOnClickListener {
            Toast.makeText(baseContext, "This method is not implemented", Toast.LENGTH_SHORT).show()
        }
    }
}