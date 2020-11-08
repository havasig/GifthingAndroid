package hu.bme.aut.android.gifthing.ui.home

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ui)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val headerView = navView.getHeaderView(0)
        val navUserFullName = headerView.findViewById(R.id.tvUserFullName) as TextView
        val navUserUsername = headerView.findViewById(R.id.tvUserUsername) as TextView
        val tvUserEmail = headerView.findViewById(R.id.tvUserEmail) as TextView

        mUserViewModel.getById(AppPreferences.currentId!!).observe(
            this,
            Observer<hu.bme.aut.android.gifthing.database.entities.User> { user ->
                if (user.firstName != null && user.lastName != null) {
                    val tempName = "(${user.firstName} ${user.lastName})"
                    navUserFullName.text = tempName
                } else {
                    navUserFullName.text = ""
                }
                navUserUsername.text = user.username
                tvUserEmail.text = user.email
            }
        )

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
}
