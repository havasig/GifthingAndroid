package hu.bme.aut.android.gifthing.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.authentication.dto.LoginRequest
import hu.bme.aut.android.gifthing.authentication.dto.LoginResponse
import hu.bme.aut.android.gifthing.database.viewModels.GiftViewModel
import hu.bme.aut.android.gifthing.services.AuthService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        showSoftKeyboard(loginUsername)

        forgotBtn.setOnClickListener {
            //TODO: forgotBtn
            Toast.makeText(applicationContext, "This method is not implemented", Toast.LENGTH_SHORT)
                .show()
        }

        loginBtn.setOnClickListener {
            if (loginUsername.text.toString() == "" ||
                loginPassword.text.toString() == ""
            ) {
                val intent = Intent(this, ErrorActivity::class.java).apply {
                    putExtra("ERROR_MESSAGE", "Fill every required field")
                }
                startActivity(intent)
            } else {
                val username = loginUsername.text.toString()
                val password = loginPassword.text.toString()

                launch {
                    val response: LoginResponse?
                    try {
                        response = login(username, password)
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {

                            val view = this@LoginActivity.currentFocus
                            view?.let { v ->
                                val imm =
                                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                            }
                            AppPreferences.currentId = 1L //TODO: set back to response.id
                            AppPreferences.token = response.accessToken
                            AppPreferences.roles = response.roles
                            AppPreferences.email = response.email
                            AppPreferences.username =
                                "Hello User" //TODO: set back to response.username
                        }
                        startActivity(intent)

                    } catch (e: HttpException) {
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

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService((Context.INPUT_METHOD_SERVICE)) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private suspend fun login(username: String, password: String): LoginResponse {
        val authService = ServiceBuilder.buildService(AuthService::class.java)
        return authService.login(
            LoginRequest(
                username,
                password
            )
        )
    }
}