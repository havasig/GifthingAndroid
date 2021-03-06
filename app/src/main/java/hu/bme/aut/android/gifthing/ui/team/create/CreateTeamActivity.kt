package hu.bme.aut.android.gifthing.ui.team.create

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.models.entities.User
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
    private lateinit var autoTextView: AutoCompleteTextView
    private val mUserViewModel: UserViewModel by viewModels()
    private val mTeamViewModel: TeamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_create_team)

        autoTextView = autoCompleteUsername as AutoCompleteTextView

        usernameAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, mutableListOf())
        autoTextView.threshold = 1
        autoTextView.setAdapter(usernameAdapter)

        mUserViewModel.getAllUsername().observe(
            this,
            Observer<List<String>> { usernames ->
                try {
                    if (usernames.contains("")) {
                        throw Exception("Server is unavailable")
                    }
                    usernames.forEach {
                        usernameAdapter.add(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Server is unavailable", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val recyclerView = usernameContainer
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = UserAdapter(this, mutableListOf())
        recyclerView.adapter = mAdapter

        mUserViewModel.getById(AppPreferences.currentId!!).observe(
            this,
            Observer<User> { currentUser ->
                try {
                    usernameAdapter.remove(currentUser.username)
                    if (!mAdapter.contains(currentUser))
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
                    Toast.makeText(baseContext, "You are already member of the group", Toast.LENGTH_SHORT).show()
                    autoCompleteUsername.setText("")
                }
                else -> onAdd(username)
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
        val newTeam = hu.bme.aut.android.gifthing.database.models.entities.Team(
            name = etTeamName.text.toString(),
            adminId = AppPreferences.currentServerId!!,
            lastFetch = null,
            lastUpdate = System.currentTimeMillis()
        )

        val memberIdList = mutableListOf<Long>()
        mAdapter.getUsers().forEach {
            memberIdList.add(it.userClientId)
        }
        mTeamViewModel.create(newTeam, memberIdList).observe(
            this,
            Observer<Boolean> { success ->
                if (success)
                    Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(
                        this,
                        "Something went wrong, try again later",
                        Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        )
    }

    private fun onAdd(username: String) {
        mUserViewModel.getByUsername(username).observe(
            this,
            Observer<User> { user ->
                try {
                    if (!mAdapter.contains(user)) {
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