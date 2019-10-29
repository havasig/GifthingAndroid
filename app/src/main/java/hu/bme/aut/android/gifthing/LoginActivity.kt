package hu.bme.aut.android.gifthing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+"


        loginBtn.setOnClickListener {
            if(loginEmail.text.toString() == "" ||
                loginPassword.text.toString() == "") {
                val intent = Intent(this, ErrorActivity::class.java).apply {
                    putExtra( "ERROR_MESSAGE","Fill every required field")
                }
                startActivity(intent)
            }else if(!loginEmail.text.toString().trim().matches(emailPattern.toRegex())) {
                val intent = Intent(this, ErrorActivity::class.java).apply {
                    putExtra( "ERROR_MESSAGE","Enter a valid email address")
                }
                startActivity(intent)
            } else {
                val email = loginEmail.text.toString()
                val password = loginPassword.text.toString()

                val checkUser = runBlocking { getUser(email) }

                var success = false
                //checkUser is not null and the password is correct
                if (checkUser!!.password.toString() == password) {
                    success = true
                }

                if (success) {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_ID", checkUser.id)
                    }
                    startActivity(intent)
                } else {
                    val intent = Intent(this, ErrorActivity::class.java).apply {
                        putExtra("ERROR_MESSAGE", "User not found")
                    }
                    startActivity(intent)
                }
            }
        }

    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private suspend fun getUser(email: String) : User? {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getUserByEmail(email)
    }
}
