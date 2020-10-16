package hu.bme.aut.android.gifthing.ui.gift.reserved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.UserWithReservedGifts
import hu.bme.aut.android.gifthing.database.models.User
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class ReservedGiftsFragment : Fragment(),
    GiftsAdapter.OnGiftSelectedListener,
    CoroutineScope by MainScope() {

    private lateinit var mAdapter: GiftsAdapter

    override fun onGiftSelected(gift: Gift) {
        val intent = Intent(activity, ReservedGiftDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", gift.giftId)
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

        val mUserViewModel: UserViewModel by viewModels()
        mUserViewModel.getUserWithReservedGifts(AppPreferences.currentId!!).observe(
            viewLifecycleOwner,
            Observer<UserWithReservedGifts> { user ->
                try {
                    mAdapter.setGifts(user.reservedGifts)
                    recyclerView.adapter = mAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context, "User or user's reserved gift list not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return rootView
    }
}