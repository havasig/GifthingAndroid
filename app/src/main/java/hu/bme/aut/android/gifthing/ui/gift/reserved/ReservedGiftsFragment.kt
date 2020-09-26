package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
                val currentUser: User
                try {
                    currentUser = getUser(AppPreferences.currentId!!)
                    mAdapter = GiftsAdapter(
                        this@ReservedGiftsFragment,
                        currentUser.reservedGifts
                    )
                    recyclerView.adapter = mAdapter
                } catch (e: HttpException) {
                    val intent = Intent(activity, ErrorActivity::class.java).apply {
                        putExtra(
                            "ERROR_MESSAGE",
                            "Current user not found"
                        )
                    }
                    activity?.startActivity(intent)
                }
            }
            return rootView
        }

        private suspend fun getUser(id: Long) : User {
            val userService = ServiceBuilder.buildService(UserService::class.java)
            return userService.findById(id)
        }
    }