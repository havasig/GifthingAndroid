package hu.bme.aut.android.gifthing.ui.team.details

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.ui.gift.my.MyGiftsActivity
import hu.bme.aut.android.gifthing.ui.user.UserGiftListActivity
import hu.bme.aut.android.gifthing.ui.user.UserListAdapter
import kotlinx.android.synthetic.main.activity_team_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

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

        val currentUserID = AppPreferences.currentId

        launch {
            try{
                //TODO: itt mi történik az indexekkel? :O
                val currentTeam = getTeam(teamId)
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
                        putExtra("ERROR_MESSAGE", "User is not member of the team")
                    }
                    startActivity(intent)
                }

                mAdapter = UserListAdapter(this@TeamDetailsActivity, tmpMembers)
                membersContainer.adapter = mAdapter
            } catch (e: HttpException){
                val intent = Intent(this@TeamDetailsActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Current team is null")
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

    private suspend fun getTeam(id: Long) : Team {
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.getById(id)
    }

    private suspend fun deleteTeam(id: Long) : Boolean {
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.deleteById(id)
    }
}