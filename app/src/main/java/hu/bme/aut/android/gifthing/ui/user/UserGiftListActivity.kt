package hu.bme.aut.android.gifthing.ui.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.gift.details.GiftToReserveDetailsActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import kotlinx.android.synthetic.main.activity_user_gift_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserGiftListActivity: AppCompatActivity(), GiftsAdapter.OnGiftSelectedListener, CoroutineScope by MainScope() {

    private lateinit var mAdapter: GiftsAdapter

    override fun onGiftSelected(gift: Gift) {
        val intent = Intent(baseContext, GiftToReserveDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", gift.id)
        }
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_gift_list)

        val userId = intent.getLongExtra("USER_ID", 0)

        giftsContainer.layoutManager = LinearLayoutManager(this)
        mAdapter = GiftsAdapter(this, mutableListOf())

        launch {
            val currentUser: User
            try {
                currentUser = getUser(userId)
                nameTv.text = currentUser.firstName + " " + currentUser.lastName
                mAdapter = GiftsAdapter(this@UserGiftListActivity, currentUser.gifts)
                giftsContainer.adapter = mAdapter
            } catch (e: HttpException) {
                val intent = Intent(this@UserGiftListActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Current user is null")
                }
                startActivity(intent)
            }
        }
    }

    private suspend fun getUser(id: Long) : User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }
}