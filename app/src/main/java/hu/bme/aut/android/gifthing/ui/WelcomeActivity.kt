package hu.bme.aut.android.gifthing.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.authentication.LoginActivity
import hu.bme.aut.android.gifthing.authentication.RegisterActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppPreferences.setup(applicationContext)

        if(AppPreferences.currentId != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        setContentView(R.layout.activity_welcome)

        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}