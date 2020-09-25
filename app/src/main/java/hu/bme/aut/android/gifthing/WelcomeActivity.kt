package hu.bme.aut.android.gifthing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.gifthing.security.LoginActivity
import hu.bme.aut.android.gifthing.security.RegisterActivity
import hu.bme.aut.android.gifthing.services.AppPreferences
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