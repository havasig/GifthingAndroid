package hu.bme.aut.android.gifthing.ui.gift.myGifts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    private lateinit var myGiftsViewModel: MyGiftsViewModel

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

        val fab: FloatingActionButton = rootView.findViewById(hu.bme.aut.android.gifthing.R.id.fabAddGift)
        fab.setOnClickListener{
            val intent = Intent(activity, CreateGiftActivity::class.java).apply {}
            activity?.startActivity(intent)
}

        launch {
            val currentUser = getUser(HomeActivity.CURRENT_USER_ID)
            if (currentUser != null) {
                val mAdapter = MyGiftsAdapter(this@MyGiftsFragment, currentUser.gifts)
                recyclerView.adapter = mAdapter
            } else {
                val intent = Intent(activity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Na itt valami komoly baj van (nincs bejelentkezve a felhasznalo)")
                }
                activity?.startActivity(intent)
            }
        }
        return rootView
    }

    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUserById(id)
    }
}