package hu.bme.aut.android.gifthing.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.authentication.LoginActivity
import hu.bme.aut.android.gifthing.authentication.RegisterActivity
import hu.bme.aut.android.gifthing.AppPreferences
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppPreferences.setup(applicationContext)

        setContentView(R.layout.activity_welcome)

        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent)
        }
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {}
            startActivity(intent)
        }
    }
}