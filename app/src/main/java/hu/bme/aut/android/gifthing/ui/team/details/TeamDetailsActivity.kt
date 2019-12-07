package hu.bme.aut.android.gifthing.ui.team.details

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.TeamService
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.ui.gift.my.MyGiftsActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import hu.bme.aut.android.gifthing.ui.user.UserGiftListActivity
import hu.bme.aut.android.gifthing.ui.user.UserListAdapter
import kotlinx.android.synthetic.main.activity_team_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TeamDetailsActivity : AppCompatActivity(),
    UserListAdapter.OnUserSelectedListener,
    CoroutineScope by MainScope() {

    private lateinit var mAdapter: UserListAdapter

    override fun onUserSelected(user: User) {
        val intent = Intent(baseContext, UserGiftListActivity::class.java).apply {
            putExtra("USER_ID", user.id)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_details)

        val teamId = intent.getLongExtra("TEAM_ID", 0)

        membersContainer.layoutManager = LinearLayoutManager(this)
        mAdapter = UserListAdapter(this, mutableListOf())

        val currentUserID = HomeActivity.CURRENT_USER_ID

        launch {
            val currentTeam = getTeam(teamId)
            if (currentTeam != null) {
                val tmpMembers = currentTeam.members
                var indx = -1
                for (i in tmpMembers.indices) {
                    if(tmpMembers[i].id == currentUserID) {
                        indx = i
                    }
                }
                if(indx != -1) {
                    tmpMembers.removeAt(indx)
                } else {
                    val intent = Intent(this@TeamDetailsActivity, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "Na itt valami komoly baj van (a felhasználó nincs benne a team-ben)")
                    }
                    startActivity(intent)
                }

                mAdapter = UserListAdapter(this@TeamDetailsActivity, tmpMembers)
                membersContainer.adapter = mAdapter
            } else {
                val intent = Intent(this@TeamDetailsActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Na itt valami komoly baj van (0 id team-et akart elkérni)")
                }
                startActivity(intent)
            }
        }
    }

    private fun onDelete(teamId: Long) {
        launch {
            Toast.makeText(baseContext, "This method is not implemented", Toast.LENGTH_SHORT).show()
            val success = deleteTeam(teamId)
            if (success) {
                val intent = Intent(this@TeamDetailsActivity, MyGiftsActivity::class.java).apply {}
                Toast.makeText(this@TeamDetailsActivity, "Deleted successfully", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@TeamDetailsActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "We could not delete this team (wtf ??? )")
                }
                startActivity(intent)
            }
        }
    }

    private suspend fun getTeam(id: Long) : Team? {
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.getById(id)
    }

    private suspend fun deleteTeam(id: Long) : Boolean {
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.deleteById(id)
    }

    private suspend fun getUser(id: Long) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }
}