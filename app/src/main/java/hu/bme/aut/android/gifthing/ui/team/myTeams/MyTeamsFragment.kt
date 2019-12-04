package hu.bme.aut.android.gifthing.ui.team.myTeams

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
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.createTeam.CreateTeamActivity
import hu.bme.aut.android.gifthing.ui.gift.GiftDetailsActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MyTeamsFragment : Fragment(),
    MyTeamsAdapter.OnTeamSelectedListener,
    CoroutineScope by MainScope() {

    private val TEAM_CREATE_REQUEST = 2
    private lateinit var mAdapter: MyTeamsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_teams, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.myTeamsContainer)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mAdapter = MyTeamsAdapter(this, mutableListOf())

        val fab: FloatingActionButton = root.findViewById(R.id.fabAddTeam)
        fab.setOnClickListener{
            val intent = Intent(activity, CreateTeamActivity::class.java).apply {}
            startActivityForResult(intent, TEAM_CREATE_REQUEST)
        }

        launch {
            val currentUser = getUser(HomeActivity.CURRENT_USER_ID)
            if (currentUser != null) {
                mAdapter = MyTeamsAdapter(this@MyTeamsFragment, currentUser.myTeams)
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

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Toast.makeText(context, "onActivityResult", Toast.LENGTH_SHORT).show()
        when(requestCode) {
            TEAM_CREATE_REQUEST -> {
                saveTeam(data)
            }
            else -> {
                Toast.makeText(context, "Create dialog result fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTeamSelected(team: Team) {
        val intent = Intent(activity, GiftDetailsActivity::class.java).apply {
            putExtra("GIFT_ID", team.id)
        }
        activity?.startActivity(intent)
    }
    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUserById(id)
    }

    private fun saveTeam(data: Intent?) {
        launch {
            if(data != null) {
                mAdapter.addTeam(data.getSerializableExtra("TEAM") as Team)
                Toast.makeText(context, "Created Successfully", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "create team null result")
                }
                activity?.startActivity(intent)
            }
        }
    }
}