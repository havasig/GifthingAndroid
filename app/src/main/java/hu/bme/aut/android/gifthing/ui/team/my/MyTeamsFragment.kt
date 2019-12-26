package hu.bme.aut.android.gifthing.ui.team.my

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
import hu.bme.aut.android.gifthing.ui.team.create.CreateTeamActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import hu.bme.aut.android.gifthing.ui.team.details.TeamDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
            val currentUser: User
            try{
                currentUser = getUser(HomeActivity.CURRENT_USER_ID)
                mAdapter = MyTeamsAdapter(this@MyTeamsFragment, currentUser.myTeams)
                recyclerView.adapter = mAdapter
            } catch (e: HttpException) {
                    val intent = Intent(activity, ErrorActivity::class.java).apply {
                        putExtra(
                            "ERROR_MESSAGE",
                            "Current user is null"
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
        val intent = Intent(activity, TeamDetailsActivity::class.java).apply {
            putExtra("TEAM_ID", team.id)
        }
        activity?.startActivity(intent)
    }
    private suspend fun getUser(id: Long) : User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }

    private fun saveTeam(data: Intent?) {
        launch {
            if(data != null) {
                mAdapter.addTeam(data.getSerializableExtra("TEAM") as Team)
                Toast.makeText(context, "Created Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}