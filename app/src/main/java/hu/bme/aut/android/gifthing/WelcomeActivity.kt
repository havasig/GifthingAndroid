package hu.bme.aut.android.gifthing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.content_main.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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