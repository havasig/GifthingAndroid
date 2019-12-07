package hu.bme.aut.android.gifthing.ui.team.create

import android.app.Activity
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
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import hu.bme.aut.android.gifthing.ui.user.UserListAdapter
import kotlinx.android.synthetic.main.dialog_create_team.*
import kotlinx.android.synthetic.main.item_email.*
import kotlinx.android.synthetic.main.item_team.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class CreateTeamActivity : AppCompatActivity(), UserListAdapter.OnUserSelectedListener, CoroutineScope by MainScope() {

    override fun onUserSelected(user: User) {
        //TODO: érintésre kitörli a listából
    }


    private lateinit var mAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_create_team)

        val recyclerView = emailContainer
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = UserListAdapter(this, mutableListOf())
        recyclerView.adapter = mAdapter
        launch {
            mAdapter.addUser(getUser(HomeActivity.CURRENT_USER_ID)!!)
        }

        btnCreateTeam.setOnClickListener {
            onCreate()
        }

        btnAdd.setOnClickListener {
            val userEmail = etEmail.text.toString()
            if(userEmail != "")
                onAdd(userEmail)
            else
                Toast.makeText(baseContext, "email field is empty", Toast.LENGTH_SHORT).show()
        }

        btnCancelTeam.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun onCreate() {
        if (etTeamName.text.toString() == "") {
            val intent = Intent(this, ErrorActivity::class.java).apply {
                putExtra("ERROR_MESSAGE", "Name is required")
            }
            startActivity(intent)
            return
        }
        val newTeam = Team()

        newTeam.adminId = HomeActivity.CURRENT_USER_ID
        newTeam.name = etTeamName.text.toString()
        newTeam.members = mAdapter.getUser()

        val tmpList = mAdapter.getUser()
        val userList = mutableListOf<User>()
        for (user in tmpList) {
            userList.add(user)
        }

        newTeam.members = userList


        launch {
            val savedTeam = createTeam(newTeam)
            Toast.makeText(baseContext, "created successfully", Toast.LENGTH_SHORT).show()

            val result = Intent().apply {
                putExtra("TEAM", savedTeam)
            }
            setResult(Activity.RESULT_OK, result)
            onBackPressed()
        }
    }

    private fun onAdd(userEmail: String) {
        launch {
            val insertedUser= getUser(userEmail)
            if(insertedUser != null && insertedUser.id != 0L) { //TODO: team response teljesen szar
                mAdapter.addUser(insertedUser)
            } else {
                Toast.makeText(baseContext, "Not existing user", Toast.LENGTH_SHORT).show()
            }
        }
        etEmail.setText("")
    }

    private suspend fun createTeam(newTeam: Team): Team{
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.create(newTeam)
    }

    private suspend fun getUser(email: String): User?{
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getByEmail(email)
    }

    private suspend fun getUser(id: Long): User?{
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getById(id)
    }
}