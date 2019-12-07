package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReservedGiftsFragment : Fragment(),
    GiftsAdapter.OnGiftSelectedListener,
    CoroutineScope by MainScope() {

        private lateinit var mAdapter: GiftsAdapter

        override fun onGiftSelected(gift: Gift) {
            val intent = Intent(activity, ReservedGiftDetailsActivity::class.java).apply {
                putExtra("GIFT_ID", gift.id)
            }
            activity?.startActivity(intent)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_reserved_gifts, container, false)
            val recyclerView: RecyclerView = rootView.findViewById(R.id.reservedGiftsContainer)
            recyclerView.layoutManager = LinearLayoutManager(activity)

            mAdapter = GiftsAdapter(this, mutableListOf())

            launch {
                val currentUser = getUser(HomeActivity.CURRENT_USER_ID)
                if (currentUser != null) {
                    mAdapter = GiftsAdapter(
                        this@ReservedGiftsFragment,
                        currentUser.reservedGifts
                    )
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
            return rootView
        }

        private suspend fun getUser(id: Long) : User? {
            val userService = ServiceBuilder.buildService(UserService::class.java)
            return userService.getById(id)
        }
    }