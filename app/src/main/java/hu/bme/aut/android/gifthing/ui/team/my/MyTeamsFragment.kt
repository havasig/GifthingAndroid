package hu.bme.aut.android.gifthing.ui.team.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams
import hu.bme.aut.android.gifthing.database.viewModels.TeamViewModel
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import hu.bme.aut.android.gifthing.ui.team.TeamEntityAdapter
import hu.bme.aut.android.gifthing.ui.team.create.CreateTeamActivity
import hu.bme.aut.android.gifthing.ui.team.details.TeamDetailsActivity
import kotlinx.android.synthetic.main.activity_team_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class MyTeamsFragment : Fragment(),
    TeamEntityAdapter.OnTeamSelectedListener,
    CoroutineScope by MainScope() {

    private val TEAM_CREATE_REQUEST = 2
    private lateinit var mAdapter: TeamEntityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_teams, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.myTeamsContainer)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mAdapter = TeamEntityAdapter(this, mutableListOf())

        val fab: FloatingActionButton = root.findViewById(R.id.fabAddTeam)
        fab.setOnClickListener {
            val intent = Intent(activity, CreateTeamActivity::class.java).apply {}
            startActivityForResult(intent, TEAM_CREATE_REQUEST)
        }

        val mTeamViewModel: TeamViewModel by viewModels()
        mTeamViewModel.allUserWithTeams.observe(
            viewLifecycleOwner,
            Observer<List<UserWithTeams>> { users ->
                try {
                    val userId = AppPreferences.currentId!!
                    val userIndex = (userId-1).toInt() //TODO: elcsúszhatnak az indexek
                    mAdapter.setTeams(users[userIndex].teams)
                    recyclerView.adapter = mAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Teams not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TEAM_CREATE_REQUEST -> saveTeam(data)
            else -> Toast.makeText(context, "Create dialog result fail", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTeamSelected(team: Team) {
        val intent = Intent(activity, TeamDetailsActivity::class.java).apply {
            putExtra("TEAM_ID", team.teamId)
        }
        activity?.startActivity(intent)
    }

    private fun saveTeam(data: Intent?) {
        launch {
            if (data != null) {
                mAdapter.addTeam(data.getSerializableExtra("TEAM") as Team)
                Toast.makeText(context, "Created Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}