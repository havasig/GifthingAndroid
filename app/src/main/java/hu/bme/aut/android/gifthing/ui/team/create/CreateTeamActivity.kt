package hu.bme.aut.android.gifthing.ui.team.create

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.entities.User
import hu.bme.aut.android.gifthing.database.viewModels.TeamViewModel
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.ui.user.UserAdapter
import kotlinx.android.synthetic.main.dialog_create_team.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


class CreateTeamActivity : AppCompatActivity(), UserAdapter.OnUserSelectedListener,
    CoroutineScope by MainScope() {

    override fun onUserSelected(user: User) {
        //TODO: érintésre kitörli a listából
    }

    private lateinit var mAdapter: UserAdapter
    private lateinit var usernameAdapter: ArrayAdapter<String>
    private lateinit var autoTextView: AppCompatAutoCompleteTextView
    private val mUserViewModel: UserViewModel by viewModels()
    private val mTeamViewModel: TeamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_create_team)

        autoTextView = autoCompleteUsername as AppCompatAutoCompleteTextView

        usernameAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, mutableListOf())
        autoTextView.threshold = 1
        autoTextView.setAdapter(usernameAdapter)

        mUserViewModel.username.observe(
            this,
            Observer<List<String>> { username ->
                try {
                    username.forEach {
                        usernameAdapter.add(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val recyclerView = usernameContainer
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = UserAdapter(this, mutableListOf())
        recyclerView.adapter = mAdapter

        mUserViewModel.currentUser.observe(
            this,
            Observer<User> { currentUser ->
                try {
                    usernameAdapter.remove(currentUser.username)
                    mAdapter.addUser(currentUser)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        btnCreateTeam.setOnClickListener {
            onTeamCreate()
        }

        btnAdd.setOnClickListener {
            when (val username = autoCompleteUsername.text.toString()) {
                "" -> Toast.makeText(baseContext, "Username is empty", Toast.LENGTH_SHORT).show()
                AppPreferences.username!! -> {
                    Toast.makeText(baseContext,"You are already member of the group",Toast.LENGTH_SHORT).show()
                    autoCompleteUsername.setText("")
                }
                else -> {
                    if (username.contains(username))
                        onAdd(username)
                    else
                        Toast.makeText(baseContext, "No user found", Toast.LENGTH_SHORT).show()

                }
            }
        }

        btnCancelTeam.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun onTeamCreate() {
        if (etTeamName.text.toString() == "") {
            Toast.makeText(baseContext, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        val newTeam = hu.bme.aut.android.gifthing.database.entities.Team(
            name = etTeamName.text.toString(),
            adminId = AppPreferences.currentId!!
        )

        val memberIdList = mutableListOf<Long>()
        mAdapter.getUsers().forEach {
            memberIdList.add(it.userId)
        }
        mTeamViewModel.insert(newTeam, memberIdList)
        onBackPressed()
    }

    private fun onAdd(username: String) {
        mUserViewModel.allUsers.observe(
            this,
            Observer<List<User>> { users ->
                try {
                    val user = users.find { it.username == username }!!
                    if(!mAdapter.contains(user)) {
                        mAdapter.addUser(user)
                        usernameAdapter.remove(user.username)
                        autoTextView.setAdapter(usernameAdapter)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "User cannot be added.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        autoCompleteUsername.setText("")
    }
}