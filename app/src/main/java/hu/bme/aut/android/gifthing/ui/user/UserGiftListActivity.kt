package hu.bme.aut.android.gifthing.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.Gift
import hu.bme.aut.android.gifthing.database.entities.UserWithOwnedGifts
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.ui.gift.details.GiftToReserveDetailsActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import kotlinx.android.synthetic.main.activity_user_gift_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.lang.Exception

class UserGiftListActivity: AppCompatActivity(), GiftsAdapter.OnGiftSelectedListener, CoroutineScope by MainScope() {

    private lateinit var mAdapter: GiftsAdapter

    override fun onGiftSelected(gift: Gift) {
        val intent = Intent(baseContext, GiftToReserveDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", gift.giftId)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_gift_list)

        val userId = intent.getLongExtra("USER_ID", 0)

        giftsContainer.layoutManager = LinearLayoutManager(this)
        mAdapter = GiftsAdapter(this, mutableListOf())

        val mUserViewModel: UserViewModel by viewModels()
        mUserViewModel.allUsersWithOwnedGifts.observe(
            this,
            Observer<List<UserWithOwnedGifts>> { users ->
                val userIndex = (userId-1).toInt() //TODO: elcsúszhatnak az indexek
                try {
                    mAdapter.setGifts(users[userIndex].ownedGifts)
                    nameTv.text = users[0].user.username
                    giftsContainer.adapter = mAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "User or user's gift list not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}