package hu.bme.aut.android.gifthing.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.Application
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.authentication.dto.SignupRequest
import hu.bme.aut.android.gifthing.authentication.dto.SignupResponse
import hu.bme.aut.android.gifthing.database.models.entities.User
import hu.bme.aut.android.gifthing.database.viewModels.UserViewModel
import hu.bme.aut.android.gifthing.services.AuthService
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+"

        var valid = true

        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                if (etEmail.text.toString() == "") {
                    emailLayout.error = "Email is required"
                    valid = false
                } else if (!etEmail.text.toString().trim().matches(emailPattern.toRegex())) {
                    emailLayout.error = "Email is not valid"
                    valid = false
                } else emailLayout.error = null
            }
        }

        etUsername.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                if (etUsername.text.toString() == "") {
                    usernameLayout.error = "Username is required"
                    valid = false
                } else usernameLayout.error = null
            }
        }

        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                if (etPassword.text.toString() == "") {
                    passwordLayout.error = "Password is required"
                    valid = false
                } else passwordLayout.error = null
            }
        }

        etPasswordAgain.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                when {
                    etPasswordAgain.text.toString() == "" -> {
                        passwordAgainLayout.error = "Password confirmation is required"
                        valid = false
                    }
                    etPassword.text.toString() != etPasswordAgain.text.toString() -> {
                        passwordAgainLayout.error = "Passwords are not matching"
                        valid = false
                    }
                    else -> passwordAgainLayout.error = null

                }
            }
        }

        okBtn.setOnClickListener {
            valid = true
            etEmail.requestFocus()
            etUsername.requestFocus()
            etPassword.requestFocus()
            etPasswordAgain.requestFocus()
            etPasswordAgain.clearFocus()

            if (!valid)
                return@setOnClickListener
            try {
                val signupRequest = SignupRequest(
                    etUsername.text.toString(),
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    etFirstName.text.toString(),
                    etLastName.text.toString()
                )
                signup(signupRequest)
                    .enqueue(object : Callback<SignupResponse> {
                        override fun onResponse(
                            call: Call<SignupResponse?>,
                            response: Response<SignupResponse?>
                        ) {
                            if (response.isSuccessful) {
                                val newUser = User(
                                    userServerId = response.body()!!.user!!.id,
                                    email = response.body()!!.user!!.email,
                                    username = response.body()!!.user!!.username,
                                    firstName = response.body()!!.user!!.firstName,
                                    lastName = response.body()!!.user!!.lastName,
                                    lastUpdate = response.body()!!.user!!.lastUpdate!!,
                                    lastFetch = System.currentTimeMillis()
                                )
                                mUserViewModel.create(newUser)
                                val intent = Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                                startActivity(intent)
                                this@RegisterActivity.finish()
                            } else {
                                try {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    Toast.makeText(
                                        applicationContext as Application,
                                        jObjError.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        override fun onFailure(call: Call<SignupResponse?>, t: Throwable) {
                            Toast.makeText(
                                applicationContext as Application,
                                "Server is unavailable",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } catch (e: Exception) {
                Toast.makeText(applicationContext as Application, e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        cancelBtn.setOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun signup(signupRequest: SignupRequest): Call<SignupResponse> {
        val authService = ServiceBuilder.buildService(AuthService::class.java)
        return authService.signup(signupRequest)
    }
}