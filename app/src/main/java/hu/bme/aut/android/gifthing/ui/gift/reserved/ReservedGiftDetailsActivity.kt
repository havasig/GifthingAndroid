package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.GiftWithOwner
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import kotlinx.android.synthetic.main.activity_reserved_gift_details.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class ReservedGiftDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val mGiftViewModel: GiftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserved_gift_details)

        val giftId = intent.getLongExtra("GIFT_ID", 0)

        mGiftViewModel.getByIdWithOwner(giftId).observe(
            this,
            Observer<GiftWithOwner> { giftWithOwner ->
                try {
                    val ownerName = if(giftWithOwner.owner.firstName != null && giftWithOwner.owner.lastName != null) {
                         "${giftWithOwner.owner.firstName} ${giftWithOwner.owner.lastName}"
                    } else {
                        giftWithOwner.owner.username
                    }
                    tvOwnerName.text = ownerName
                    tvGiftName.text = giftWithOwner.gift.name
                    tvGiftDescription.text = giftWithOwner.gift.description ?: ""
                    tvGiftPrice.text = giftWithOwner.gift.price?.toString() ?: ""
                    tvGiftLink.text = giftWithOwner.gift.link ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Gift with owner not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}