package hu.bme.aut.android.gifthing.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.gift.details.GiftToReserveDetailsActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import kotlinx.android.synthetic.main.activity_user_gift_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class UserGiftListActivity: AppCompatActivity(), GiftsAdapter.OnGiftSelectedListener, CoroutineScope by MainScope() {

    private lateinit var mAdapter: GiftsAdapter

    override fun onGiftSelected(gift: Gift) {
        val intent = Intent(baseContext, GiftToReserveDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", gift.id)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_gift_list)

        val userId = intent.getLongExtra("USER_ID", 0)

        giftsContainer.layoutManager = LinearLayoutManager(this)
        mAdapter = GiftsAdapter(this, mutableListOf())

        launch {
            val currentUser = getUser(userId)
            if (currentUser  != null) {
                currentUser.name?.let { nameTv.text = it } ?: let{nameTv.text = currentUser.email}
                mAdapter = GiftsAdapter(this@UserGiftListActivity, currentUser.gifts)
                giftsContainer.adapter = mAdapter
            } else {
                val intent = Intent(this@UserGiftListActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Na itt valami komoly baj van (0 id user-t akart elkérni)")
                }
                startActivity(intent)
            }
        }
    }

    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }
}