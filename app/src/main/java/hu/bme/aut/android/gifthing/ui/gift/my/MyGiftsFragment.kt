package hu.bme.aut.android.gifthing.ui.gift.my

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
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.TeamService
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Gift
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.gift.CreateGiftActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftsAdapter
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MyGiftsFragment : Fragment(),
    GiftsAdapter.OnGiftSelectedListener,
    CoroutineScope by MainScope() {

    private val GIFT_CREATE_REQUEST = 1
    private lateinit var mAdapter: GiftsAdapter

    override fun onGiftSelected(gift: Gift) {
        val intent = Intent(activity, MyGiftDetailsActivity::class.java).apply {
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

        mAdapter = GiftsAdapter(this, mutableListOf())

        launch {
            val currentUser = try {
                getUser(HomeActivity.CURRENT_USER_ID)
            } catch (e: HttpException) {
                if(e.code() != 404) {
                    val intent = Intent(activity, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Valami hiba van, de nem 404")
                    }
                    activity?.startActivity(intent)
                }
                null
            }
            if (currentUser != null) {
                mAdapter = GiftsAdapter(
                    this@MyGiftsFragment,
                    currentUser.gifts
                )
                recyclerView.adapter = mAdapter
            } else {
                val intent = Intent(activity, ErrorActivity::class.java).apply {
                    putExtra(
                        "ERROR_MESSAGE",
                        "User is not logged in"
                    )
                }
                activity?.startActivity(intent)
            }
        }

        val fab: FloatingActionButton = rootView.findViewById(hu.bme.aut.android.gifthing.R.id.fabAddGift)
        fab.setOnClickListener{
            val intent = Intent(activity, CreateGiftActivity::class.java).apply {}
            startActivityForResult(intent, GIFT_CREATE_REQUEST)
        }
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            GIFT_CREATE_REQUEST -> {
                saveGift(data)
            }
            else -> {
                Toast.makeText(context, "Create dialog result fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getUser(id: Long) : User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }

    private suspend fun getTeam(id: Long) : Team? {
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.getById(id)
    }

    private fun saveGift(data: Intent?) {
        launch {
            if(data != null) {
                mAdapter.addGift(data.getSerializableExtra("GIFT") as Gift)
                Toast.makeText(context, "Created Successfully", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(context, "cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }