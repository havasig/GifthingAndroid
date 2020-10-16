package hu.bme.aut.android.gifthing.ui.team.my

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
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.Team
import hu.bme.aut.android.gifthing.database.entities.UserWithTeams
import hu.bme.aut.android.gifthing.database.viewModels.TeamViewModel
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.ui.team.TeamEntityAdapter
import hu.bme.aut.android.gifthing.ui.team.create.CreateTeamActivity
import hu.bme.aut.android.gifthing.ui.team.details.TeamDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MyTeamsFragment : Fragment(),
    TeamEntityAdapter.OnTeamSelectedListener,
    CoroutineScope by MainScope() {

    private lateinit var mAdapter: TeamEntityAdapter
    private val mUserViewModel: UserViewModel by viewModels()

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
            val intent = Intent(activity, CreateTeamActivity::class.java)
            startActivity(intent)
        }

        mUserViewModel.myTeams.observe(
            viewLifecycleOwner,
            Observer<UserWithTeams> { user ->
                try {
                    mAdapter.setTeams(user.teams)
                    recyclerView.adapter = mAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Teams not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        return root
    }

    override fun onTeamSelected(team: Team) {
        val intent = Intent(activity, TeamDetailsActivity::class.java).apply {
            putExtra("TEAM_ID", team.teamId)
        }
        activity?.startActivity(intent)
    }
}