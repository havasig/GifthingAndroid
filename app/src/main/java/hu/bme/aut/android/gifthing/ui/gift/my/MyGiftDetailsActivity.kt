package hu.bme.aut.android.gifthing.ui.gift.my

import android.content.DialogInterface
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import kotlinx.android.synthetic.main.activity_my_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
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
                        tvOwnerName.text = getString(R.string.you)
                        tvGiftName.text = gift.name
                        gift.price?.let { tvGiftPrice.text = it.toString() } ?: run { priceLL.visibility = View.GONE }
                        gift.description?.let { tvGiftDescription.text = it } ?: run { descriptionLL.visibility = View.GONE }
                        gift.link?.let { tvGiftLink.text = it } ?: run { linkLL.visibility = View.GONE }
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
                            mGiftViewModel.delete(currentGift.giftClientId).observe(
                                this,
                                Observer<Boolean> { success ->
                                    if (success) {
                                        deleted = true
                                        Toast.makeText(baseContext, "Deleted successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else
                                        Toast.makeText(baseContext, "Something went wrong, try again later", Toast.LENGTH_SHORT).show()
                                }
                            )
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