package hu.bme.aut.android.gifthing.ui.gift

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.database.models.entities.Gift
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import kotlinx.android.synthetic.main.dialog_create_gift.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class CreateGiftActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val mGiftViewModel: GiftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(hu.bme.aut.android.gifthing.R.layout.dialog_create_gift)
        setFinishOnTouchOutside(false)
        showSoftKeyboard(etGiftName)

        btnCreate.setOnClickListener {
            if (etGiftName.text.toString() == "") {
                Toast.makeText(baseContext, "Name is required", Toast.LENGTH_SHORT).show()
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

            val newGift = Gift(
                name = etGiftName.text.toString(),
                price = price,
                description = description,
                link = link,
                reservedBy = null,
                owner = AppPreferences.currentId!!,
                lastUpdate = System.currentTimeMillis(),
                lastFetch = System.currentTimeMillis()
            )

            mGiftViewModel.create(newGift).observe(
                this, Observer<Boolean>{ success ->
                    if(success)
                        Toast.makeText(baseContext, "Gift created", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(baseContext, "Something went wrong, try again later", Toast.LENGTH_SHORT).show()

                    finish()
                }
            )

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
