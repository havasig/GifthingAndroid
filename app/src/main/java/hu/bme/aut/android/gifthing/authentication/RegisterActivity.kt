package hu.bme.aut.android.gifthing.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.services.ServiceBuilder
import hu.bme.aut.android.gifthing.authentication.dto.SignupRequest
import hu.bme.aut.android.gifthing.authentication.dto.SignupResponse
import hu.bme.aut.android.gifthing.services.AuthService
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+"

        okBtn.setOnClickListener {
            try {
                if (etEmail.text.toString() == "" ||
                    etPassword.text.toString() == "" ||
                    etPasswordAgain.text.toString() == "" ||
                    etUsername.text.toString() == ""
                ) {
                    throw Exception("Fill every required field")
                } else if (etPassword.text.toString() != etPasswordAgain.text.toString()) {
                    throw Exception("The passwords are not matching")
                } else if (!etEmail.text.toString().trim().matches(emailPattern.toRegex())) {
                    throw Exception("Enter a valid email address")
                } else {
                    try {
                        val signupRequest = SignupRequest(
                            etUsername.text.toString(),
                            etEmail.text.toString(),
                            etPassword.text.toString(),
                            etFirstName.text.toString(),
                            etLastName.text.toString()
                        )
                        val call: Call<SignupResponse> = signup(signupRequest)
                        call.enqueue(object : Callback<SignupResponse> {
                            override fun onResponse(
                                call: Call<SignupResponse?>,
                                response: Response<SignupResponse?>
                            ) {
                                if (response.isSuccessful) {
                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    try {
                                        val jObjError = JSONObject(response.errorBody()!!.string())
                                        Toast.makeText(applicationContext,jObjError.getString("message"),Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            override fun onFailure(
                                call: Call<SignupResponse?>,
                                t: Throwable
                            ) {
                                Toast.makeText(applicationContext,"Something went wrong, try again later.",Toast.LENGTH_SHORT).show()
                            }
                        })
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext,e.message,Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext,e.message,Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun signup(signupRequest: SignupRequest) : Call<SignupResponse> {
        val authService = ServiceBuilder.buildService(AuthService::class.java)
        return authService.signup(signupRequest)
    }
}