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


class CreateGiftActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    /*    private var listener: CreateGiftDialogListener? = null

            private var nameEditText: EditText? = null

            private val contentView: View
                get() {
                    val view = LayoutInflater.from(context).inflate(hu.bme.aut.android.gifthing.R.layout.dialog_create_gift, null)
                    nameEditText = view.findViewById(hu.bme.aut.android.gifthing.R.id.giftName)
                    return view
                }

            interface CreateGiftDialogListener {
                fun onGiftCreated(gift: Gift)
            }

            override fun onCreate(@Nullable savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                if (activity is CreateGiftDialogListener) {
                    listener = activity as CreateGiftDialogListener?
                } else {
                    throw RuntimeException("Fragment must implement CreateGiftDialogListener interface!")
                }
            }

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                return AlertDialog.Builder(requireContext())
                    .setTitle("New Gift")
                    .setView(contentView)
                    .setPositiveButton(
                        hu.bme.aut.android.gifthing.R.string.create
                    )
                    { _, _ ->

                        //TODO: létre kell hozni az ajándékot
                        val newGift = Gift()
                        newGift.name ="asd"

                        listener!!.onGiftCreated(
                            newGift
                        )
                    }
                    .setNegativeButton(hu.bme.aut.android.gifthing.R.string.cancel, null)
                    .create()
            }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(hu.bme.aut.android.gifthing.R.layout.dialog_create_gift)
        setFinishOnTouchOutside(false)
        //TODO: appbar

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
                if (!URLUtil.isValidUrl(etGiftLink.text.toString())) {
                    val intent = Intent(this, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Add a valid link (or leave it empty")
                    }
                    startActivity(intent)
                    return@setOnClickListener
                } else {
                    link = etGiftLink.text.toString()
                }
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

                val savedGift = createGift(newGift)

                val result = Intent().apply {
                    putExtra("GIFT", savedGift)
                }
                setResult(Activity.RESULT_OK, result)

                finish()
            }


            /*val owner = getUser(HomeActivity.CURRENT_USER_ID)
                if(owner == null) {
                    val intent = Intent(this@CreateGiftActivity, ErrorActivity::class.java).apply {
                        putExtra( "ERROR_MESSAGE","Null owner, nagy a baj")
                    }
                    startActivity(intent)
                } else {
                    newGift.owner = owner*/

            /*val successSaveUser = saveUser(owner)
                    if(!successSaveUser) {
                        Toast.makeText(this@CreateGiftActivity, "Could not update user", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this@CreateGiftActivity, "Gift created successfully", Toast.LENGTH_SHORT).show()*/
        }

        btnCancel.setOnClickListener{
            super.onBackPressed()
        }
    }

    private suspend fun createGift(newGift: Gift) : Gift {
        val giftService = ServiceBuilder.buildService(GiftService::class.java)
        return giftService.create(newGift)
    }

    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUserById(id)
    }

    private suspend fun saveUser(user: User) : Boolean {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.updateUser(user)
    }
}
