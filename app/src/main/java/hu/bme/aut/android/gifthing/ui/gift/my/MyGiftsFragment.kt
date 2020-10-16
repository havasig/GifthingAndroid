package hu.bme.aut.android.gifthing.ui.gift.my

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.ui.gift.CreateGiftActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.lang.Exception

class MyGiftsFragment : Fragment(),
    GiftsAdapter.OnGiftSelectedListener,
    CoroutineScope by MainScope() {

    private lateinit var mAdapter: GiftsAdapter

    override fun onGiftSelected(gift: hu.bme.aut.android.gifthing.database.entities.Gift) {
        val intent = Intent(activity, MyGiftDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", gift.giftId)
        }
        activity?.startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(hu.bme.aut.android.gifthing.R.layout.fragment_my_gifts,container,false)
        val recyclerView: RecyclerView = rootView.findViewById(hu.bme.aut.android.gifthing.R.id.myGiftsContainer)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mAdapter = GiftsAdapter(this, mutableListOf())

        val mUserViewModel: UserViewModel by viewModels()

        try {
            mUserViewModel.getUserWithOwnedGifts(AppPreferences.currentId!!).observe(
                viewLifecycleOwner,
                Observer<hu.bme.aut.android.gifthing.database.entities.UserWithOwnedGifts> { user ->
                    mAdapter.setGifts(user.ownedGifts)
                }
            )
        } catch (e: Exception) {
            Toast.makeText(context, "lol", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = mAdapter

        val fab: FloatingActionButton =
            rootView.findViewById(hu.bme.aut.android.gifthing.R.id.fabAddGift)
        fab.setOnClickListener {
            val intent = Intent(activity, CreateGiftActivity::class.java).apply {}
            startActivity(intent)
        }
        return rootView
    }
}