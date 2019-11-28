package hu.bme.aut.android.gifthing.ui.gift.myGifts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftDetailsActivity
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.gift.CreateGiftActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch




class MyGiftsFragment : Fragment(),
    MyGiftsAdapter.OnGiftSelectedListener,
    CoroutineScope by MainScope() {

    private val GIFT_CREATE_REQUEST = 1
    private lateinit var mAdapter: MyGiftsAdapter

    override fun onGiftSelected(gift: Gift) {
        val intent = Intent(activity, GiftDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", gift.id)
        }
        activity?.startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(hu.bme.aut.android.gifthing.R.layout.fragment_my_gifts, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(hu.bme.aut.android.gifthing.R.id.myGiftsContainer)
        recyclerView.layoutManager = LinearLayoutManager(activity)


        launch {

            val currentUser = getUser(HomeActivity.CURRENT_USER_ID)
            if (currentUser != null) {
                mAdapter = MyGiftsAdapter(this@MyGiftsFragment, currentUser.gifts)
                recyclerView.adapter = mAdapter
            } else {
                val intent = Intent(activity, ErrorActivity::class.java).apply {
                    putExtra(
                        "ERROR_MESSAGE",
                        "Na itt valami komoly baj van (nincs bejelentkezve a felhasznalo)"
                    )
                }
                activity?.startActivity(intent)
            }

        }


        val fab: FloatingActionButton = rootView.findViewById(hu.bme.aut.android.gifthing.R.id.fabAddGift)
        fab.setOnClickListener{
            val intent = Intent(activity, CreateGiftActivity::class.java).apply {}
            startActivityForResult(intent, GIFT_CREATE_REQUEST)
            //activity?.startActivity(intent)


            /*CreateGiftActivity()
                .show(
                    fragmentManager!!,
                    CreateGiftActivity::class.java.simpleName
                )*/

        }
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Toast.makeText(context, "onActivityResult", Toast.LENGTH_SHORT).show()
        when(requestCode) {
            GIFT_CREATE_REQUEST -> {
                saveGift(data)
            }
            else -> {

                Toast.makeText(context, "nosaveGift", Toast.LENGTH_SHORT).show()

                /*val intent = Intent(activity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "nem volt jo a create visszateresi erteke")
                }
                activity?.startActivity(intent)
            */
            }
        }
    }

    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUserById(id)
    }

    private fun saveGift(data: Intent?) {
        Toast.makeText(context, "saveGift", Toast.LENGTH_SHORT).show()

        launch {
            if(data != null) {
                mAdapter.addGift(data.getSerializableExtra("GIFT") as Gift)
                Toast.makeText(context, "je", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "null-al tér vissza a create gift")
                }
                activity?.startActivity(intent)
            }




        }
    }
}