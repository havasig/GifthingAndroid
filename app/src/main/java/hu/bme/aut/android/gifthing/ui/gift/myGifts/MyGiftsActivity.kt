package hu.bme.aut.android.gifthing.ui.gift.myGifts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView


class MyGiftsActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(hu.bme.aut.android.gifthing.R.layout.activity_ui)
        val toolbar: Toolbar = findViewById(hu.bme.aut.android.gifthing.R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: NavigationView = findViewById(hu.bme.aut.android.gifthing.R.id.nav_view)
        val drawerLayout: DrawerLayout = findViewById(hu.bme.aut.android.gifthing.R.id.drawer_layout)

        val navController = findNavController(hu.bme.aut.android.gifthing.R.id.nav_host_fragment)
        // Passing each main ID as a set of Ids because each
        // main should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                hu.bme.aut.android.gifthing.R.id.nav_home,
                hu.bme.aut.android.gifthing.R.id.nav_my_groups,
                hu.bme.aut.android.gifthing.R.id.nav_my_gifts,
                hu.bme.aut.android.gifthing.R.id.nav_my_invites,
                hu.bme.aut.android.gifthing.R.id.nav_reserved_gifts,
                hu.bme.aut.android.gifthing.R.id.nav_create_group,
                hu.bme.aut.android.gifthing.R.id.nav_settings,
                hu.bme.aut.android.gifthing.R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(hu.bme.aut.android.gifthing.R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
