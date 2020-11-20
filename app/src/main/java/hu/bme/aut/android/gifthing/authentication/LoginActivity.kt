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
import hu.bme.aut.android.gifthing.ui.ErrorActivity
import hu.bme.aut.android.gifthing.ui.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val mUserViewModel: UserViewModel by viewModels()

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

                mUserViewModel.login(username, password).observe(
                    this,
                    Observer<LoginData> { result ->
                        if (result.id != -1L) {
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
                            startActivity(intent)
                            this.finish()
                        } else {
                            val intent =
                                Intent(this@LoginActivity, ErrorActivity::class.java).apply {
                                    putExtra("ERROR_MESSAGE", "User not found")
                                }
                            startActivity(intent)
                        }
                    }
                )
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
}