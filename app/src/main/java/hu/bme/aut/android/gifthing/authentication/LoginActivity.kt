package hu.bme.aut.android.gifthing.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.authentication.dto.LoginData
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppPreferences.setup(applicationContext)

        if (AppPreferences.currentId != null)
            startActivity(Intent(this, HomeActivity::class.java))

        setContentView(R.layout.activity_login)

        showSoftKeyboard(loginUsername)

        forgotBtn.setOnClickListener {
            //TODO: forgotBtn
            Toast.makeText(applicationContext, "This method is not implemented", Toast.LENGTH_SHORT)
                .show()
        }

        var wtfCounter = 0

        loginBtn.setOnClickListener {
            var valid = true
            if (loginUsername.text.toString() == "") {
                usernameLayout.error = "Username is required"
                valid = false
            } else {
                usernameLayout.error = null
            }
            if (loginPassword.text.toString() == "") {
                passwordLayout.error = "Password is required"
                valid = false
            } else {
                passwordLayout.error = null
            }
            if (!valid)
                return@setOnClickListener

            val username = loginUsername.text.toString()
            val password = loginPassword.text.toString()

            mUserViewModel.login(username, password).observe(
                this,
                Observer<LoginData> { result ->
                    when {
                        result.id > 0 -> {
                            if (wtfCounter == 0) {
                                AppPreferences.currentId = result.id
                                AppPreferences.email = result.email
                                AppPreferences.roles = result.roles
                                AppPreferences.token = result.accessToken
                                AppPreferences.username = result.username

                                val intent = Intent(this, HomeActivity::class.java).apply {
                                    val view = this@LoginActivity.currentFocus
                                    view?.let { v ->
                                        val imm =
                                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                        imm?.hideSoftInputFromWindow(v.windowToken, 0)
                                    }
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                                wtfCounter++
                                startActivity(intent)
                            }
                        }
                        result.id == -1L -> {
                            Toast.makeText(
                                applicationContext,
                                "Incorrect username or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        result.id == -2L -> {
                            Toast.makeText(
                                applicationContext,
                                "Server is unavailable, try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }


        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
}