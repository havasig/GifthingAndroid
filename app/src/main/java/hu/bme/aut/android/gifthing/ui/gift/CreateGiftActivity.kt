package hu.bme.aut.android.gifthing.ui.gift

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.database.models.Gift
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import hu.bme.aut.android.gifthing.services.GiftService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import kotlinx.android.synthetic.main.dialog_create_gift.*
import kotlinx.android.synthetic.main.gift_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class CreateGiftActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(hu.bme.aut.android.gifthing.R.layout.dialog_create_gift)
        setFinishOnTouchOutside(false)
        showSoftKeyboard(etGiftName)

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

            val price = if (etGiftPrice.text.toString() != "") {
                Integer.parseInt(etGiftPrice.text.toString())
            } else null

            val description = if (etGiftDescription.text.toString() != "") {
                etGiftDescription.text.toString()
            } else null

            val newGift = hu.bme.aut.android.gifthing.database.entities.Gift(
                name = etGiftName.text.toString(),
                price = price,
                description = description,
                link = link,
                reservedBy = null,
                owner = AppPreferences.currentId!!
            )

            val mGiftViewModel: GiftViewModel by viewModels()
            mGiftViewModel.insert(newGift)
            Toast.makeText(baseContext, "Gift created", Toast.LENGTH_SHORT).show()
            finish()

/*
            launch {
                try {
                    val currentUserId = AppPreferences.currentId
                    if (currentUserId == 0L) {
                        throw Exception("User not logged in")
                    }
                    newGift.owner = currentUserId
                    val savedGift = createGift(newGift)

                    val result = Intent().apply {
                        putExtra("GIFT", savedGift)
                    }
                    setResult(Activity.RESULT_OK, result)
                } catch (e: HttpException) {
                    val intent = Intent(this@CreateGiftActivity, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Something went wrong, try again later.")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(this@CreateGiftActivity, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", e.message)
                    }
                    startActivity(intent)
                }


                finish()
            }
 */
        }

        btnCancel.setOnClickListener {
            Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService((Context.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }
}
