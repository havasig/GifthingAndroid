package hu.bme.aut.android.gifthing.ui.team.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import hu.bme.aut.android.gifthing.services.UserService
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.ui.user.UserListAdapter
import kotlinx.android.synthetic.main.dialog_create_team.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class CreateTeamActivity : AppCompatActivity(), UserListAdapter.OnUserSelectedListener, CoroutineScope by MainScope() {

    override fun onUserSelected(user: User) {
        //TODO: érintésre kitörli a listából
    }


    private lateinit var mAdapter: UserListAdapter
    private  lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_create_team)


        val recyclerView = emailContainer
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = UserListAdapter(this, mutableListOf())
        recyclerView.adapter = mAdapter

        launch {
            try {
                mAdapter.addUser(getUser(AppPreferences.currentId!!))
            } catch (e: HttpException) {
                val intent = Intent(this@CreateTeamActivity, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "User is not logged in")
                }
                startActivity(intent)
            }
        }

        btnCreateTeam.setOnClickListener {
            onCreate()
        }

        btnAdd.setOnClickListener {
            when(val userEmail = etEmail.text.toString()) {
                "" -> Toast.makeText(baseContext, "email field is empty", Toast.LENGTH_SHORT).show()
                currentUserEmail -> {
                    Toast.makeText(baseContext, "You are already member of the group", Toast.LENGTH_SHORT).show()
                    etEmail.setText("")
                }
                else -> onAdd(userEmail)
            }
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

        newTeam.adminId = AppPreferences.currentId
        newTeam.name = etTeamName.text.toString()
        newTeam.members = mAdapter.getUsers()

        val tmpList = mAdapter.getUsers()
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
            val insertedUser: User
            try {
                insertedUser = getUser(userEmail)
                if(insertedUser.id != 0L) { //TODO: team response teljesen szar
                    mAdapter.addUser(insertedUser)
                }
            } catch (e: HttpException) {
                Toast.makeText(baseContext, "Not existing user", Toast.LENGTH_SHORT).show()
            }
        }
        etEmail.setText("")
    }

    private suspend fun createTeam(newTeam: Team): Team{
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.create(newTeam)
    }

    private suspend fun getUser(email: String): User{
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.findByEmail(email)
    }

    private suspend fun getUser(id: Long): User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        val tmp = userService.findById(id)
        currentUserEmail = tmp.email
        return tmp
    }
}