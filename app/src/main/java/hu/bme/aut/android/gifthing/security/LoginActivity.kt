package hu.bme.aut.android.gifthing.security

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import hu.bme.aut.android.gifthing.ErrorActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.Services.ServiceBuilder
import hu.bme.aut.android.gifthing.Services.UserService
import hu.bme.aut.android.gifthing.models.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import android.app.Activity





class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showSoftKeyboard(loginEmail)

        forgotBtn.setOnClickListener {
            //TODO: forgotBtn
            Toast.makeText(applicationContext, "This method is not implemented", Toast.LENGTH_SHORT).show()
        }

        loginBtn.setOnClickListener {
            if(loginEmail.text.toString() == "" ||
                loginPassword.text.toString() == "") {
                val intent = Intent(this, ErrorActivity::class.java).apply {
                    putExtra( "ERROR_MESSAGE","Fill every required field")
                }
                startActivity(intent)
            } else {
                val email = loginEmail.text.toString()
                val password = loginPassword.text.toString()

                launch {
                    val checkUser : User?
                    try {
                        checkUser = getUser(email)
                        if (checkUser.password.toString() == password) {
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {

                                val view = this@LoginActivity.currentFocus
                                view?.let { v ->
                                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                                }

                                putExtra("USER_ID", checkUser.id)
                            }
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@LoginActivity, ErrorActivity::class.java).apply {
                                putExtra("ERROR_MESSAGE", "Not matching password and email")
                            }
                            startActivity(intent)
                        }
                    } catch (e : HttpException){
                        val intent = Intent(this@LoginActivity, ErrorActivity::class.java).apply {
                            putExtra("ERROR_MESSAGE", "User not found")
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun showSoftKeyboard(view: View){
        if(view.requestFocus()){
            val imm = getSystemService((Context.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private suspend fun getUser(email: String) : User {
        val userService = ServiceBuilder.buildService(UserService::class.java)
        return userService.getByEmail(email)
    }
}