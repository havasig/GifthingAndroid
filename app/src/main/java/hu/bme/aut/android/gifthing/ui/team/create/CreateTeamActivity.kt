package hu.bme.aut.android.gifthing.ui.team.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.models.Team
import hu.bme.aut.android.gifthing.models.User
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.TeamService
import hu.bme.aut.android.gifthing.services.UserService
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.ui.user.UserListAdapter
import kotlinx.android.synthetic.main.dialog_create_team.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class CreateTeamActivity : AppCompatActivity(), UserListAdapter.OnUserSelectedListener,
    CoroutineScope by MainScope() {

    override fun onUserSelected(user: User) {
        //TODO: érintésre kitörli a listából
    }

    private lateinit var mAdapter: UserListAdapter
    private lateinit var usernameAdapter: ArrayAdapter<String>
    private lateinit var autoTextView: AppCompatAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_create_team)

        var usernames: List<String> = ArrayList()

        autoTextView = autoCompleteUsername as AppCompatAutoCompleteTextView

        usernameAdapter =
            ArrayAdapter(this, android.R.layout.select_dialog_item, mutableListOf())
        autoTextView.threshold = 1
        autoTextView.setAdapter(usernameAdapter)
        launch {
            try {
                usernames = getUsername()
                usernames.forEach {
                        usernameAdapter.add(it)
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext,"Something went wrong, try again later.",Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        val recyclerView = usernameContainer
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = UserListAdapter(this, mutableListOf())
        recyclerView.adapter = mAdapter

        launch {
            try {
                mAdapter.addUser(getUser(AppPreferences.currentId!!))
            } catch (e: Exception) {
                Toast.makeText(applicationContext,"Something went wrong, try again later.",Toast.LENGTH_SHORT).show()
            }
        }

        btnCreateTeam.setOnClickListener {
            onTeamCreate()
        }

        btnAdd.setOnClickListener {
            when (val username = autoCompleteUsername.text.toString()) {
                "" -> Toast.makeText(baseContext, "Username is empty", Toast.LENGTH_SHORT).show()
                AppPreferences.username!! -> {
                    Toast.makeText( baseContext,"You are already member of the group",Toast.LENGTH_SHORT).show()
                    autoCompleteUsername.setText("")
                }
                else -> {
                    if (usernames.contains(username))
                        onAdd(username)
                    else
                        Toast.makeText( baseContext,"No user found",Toast.LENGTH_SHORT).show()

                }
            }
        }

        btnCancelTeam.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun onTeamCreate() {
        if (etTeamName.text.toString() == "") {
            Toast.makeText( baseContext,"Name is required",Toast.LENGTH_SHORT).show()
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
            try {
                val savedTeam = createTeam(newTeam)
                Toast.makeText(baseContext, "created successfully", Toast.LENGTH_SHORT).show()

                val result = Intent().apply {
                    putExtra("TEAM", savedTeam)
                }
                setResult(Activity.RESULT_OK, result)
                onBackPressed()
            } catch (e: Exception) {
                Toast.makeText(baseContext, "Something went wrong, try again later.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun onAdd(username: String) {
        launch {
            val insertedUser: User
            try {
                insertedUser = findByUsername(username)
                var member = false
                mAdapter.getUsers().forEach {
                    if (it.username == insertedUser.username)
                        member = true
                }
                if (!member) {
                    mAdapter.addUser(insertedUser)
                    usernameAdapter.remove(insertedUser.username)
                    autoTextView.setAdapter(usernameAdapter)
                }
                else
                    Toast.makeText(baseContext, "Already member", Toast.LENGTH_SHORT).show()
            } catch (e: HttpException) {
                Toast.makeText(baseContext, "Not existing user", Toast.LENGTH_SHORT).show()
            }
        }
        autoCompleteUsername.setText("")
    }

    private suspend fun createTeam(newTeam: Team): Team {
        val teamService = ServiceBuilder.buildService(TeamService::class.java)
        return teamService.create(newTeam)
    }

    private suspend fun findByUsername(username: String): User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.findByUsername(username)
    }

    private suspend fun getUser(id: Long): User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.findById(id)
    }

    private suspend fun getUsername(): ArrayList<String> {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUsernames()
    }
}