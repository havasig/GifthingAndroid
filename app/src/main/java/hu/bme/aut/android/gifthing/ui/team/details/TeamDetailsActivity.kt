package hu.bme.aut.android.gifthing.ui.team.details

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.models.entities.TeamWithMembers
import hu.bme.aut.android.gifthing.database.models.entities.User
import hu.bme.aut.android.gifthing.database.viewModels.TeamViewModel
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.ui.gift.my.MyGiftsActivity
import hu.bme.aut.android.gifthing.ui.user.UserAdapter
import hu.bme.aut.android.gifthing.ui.user.UserGiftListActivity
import kotlinx.android.synthetic.main.activity_team_details.*

class TeamDetailsActivity : AppCompatActivity(),
    UserAdapter.OnUserSelectedListener {

    private lateinit var mAdapter: UserAdapter
    private val mTeamViewModel: TeamViewModel by viewModels()

    override fun onUserSelected(user: User) {
        val intent = Intent(baseContext, UserGiftListActivity::class.java).apply {
            putExtra("USER_ID", user.userClientId)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_details)


        membersContainer.layoutManager = LinearLayoutManager(this)
        mAdapter = UserAdapter(this, mutableListOf())

        val currentUserId = AppPreferences.currentId
        val teamId = intent.getLongExtra("TEAM_ID", 0)

        mTeamViewModel.getTeamWithMembers(teamId).observe(
            this,
            Observer<TeamWithMembers> { team ->
                try {
                    val memberList = team.members.filter { it.userClientId != currentUserId }
                    mAdapter.setUsers(memberList)
                    membersContainer.adapter = mAdapter
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Team not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun onDelete(teamId: Long) {
        Toast.makeText(baseContext, "This method is not implemented", Toast.LENGTH_SHORT).show()
        val success = true //TODO: implement deleteTeam(teamId)
        if (success) {
            val intent = Intent(this@TeamDetailsActivity, MyGiftsActivity::class.java).apply {}
            Toast.makeText(this@TeamDetailsActivity, "Deleted successfully", Toast.LENGTH_SHORT)
                .show()
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