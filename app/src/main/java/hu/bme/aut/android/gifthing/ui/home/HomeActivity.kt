package hu.bme.aut.android.gifthing.ui.home

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.models.User
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ui)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val currentUserId = AppPreferences.currentId!!

        val navView: NavigationView = findViewById(R.id.nav_view)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)


        val headerView = navView.getHeaderView(0)
        val navUserFullName = headerView.findViewById(R.id.tvUserFullName) as TextView
        val navUserUsername = headerView.findViewById(R.id.tvUserUsername) as TextView
        val tvUserEmail = headerView.findViewById(R.id.tvUserEmail) as TextView

        launch {
            val currentUser = getUser(currentUserId)

            if (currentUser.firstName != null || currentUser.lastName != null) {
                val tempName = "(${currentUser.firstName} ${currentUser.lastName})"
                navUserFullName.text = tempName
            } else {
                navUserFullName.text = ""
            }
            navUserUsername.text = currentUser.username
            tvUserEmail.text = currentUser.email
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each main ID as a set of Ids because each
        // main should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_my_teams,
                R.id.nav_my_gifts,
                R.id.nav_my_invites,
                R.id.nav_reserved_gifts,
                R.id.nav_create_team,
                R.id.nav_settings,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private suspend fun getUser(id: Long): User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.findById(id)
    }
}
