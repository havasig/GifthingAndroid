package hu.bme.aut.android.gifthing

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.User
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope(){

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        launch(Dispatchers.Main) {
            var user0 = loadUser(0)
            var loadedUser = loadUser(1)
            tvHelloWorld.text = loadedUser.password
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }




    private suspend fun loadUser(id: Int) : User{
        var user = User()

        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUser(id)
        /*val requestCall = userService.getUser(id)

        requestCall.enqueue(object: Callback<User> {
            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                tvHelloWorld.text = t.toString()
            }

            override fun onResponse(call: retrofit2.Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    val userData = response.body()!!

                    tvHelloWorld.text = userData.password

                    user.id = userData.id
                    user.name = userData.name
                    user.password = userData.password

                }
            }
        })*/

        return user
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
