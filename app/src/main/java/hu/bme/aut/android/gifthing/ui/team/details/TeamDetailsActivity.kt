package hu.bme.aut.android.gifthing.ui.team.details

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.TeamService
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.ui.gift.myGifts.MyGiftsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TeamDetailsActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_details)

        val teamId = intent.getLongExtra("TEAM_ID", 0)

        launch {
            val currentTeam = getTeam(teamId)
            if (currentTeam != null) {
                //TODO: tvGiftName.text = currentGift.name
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
}