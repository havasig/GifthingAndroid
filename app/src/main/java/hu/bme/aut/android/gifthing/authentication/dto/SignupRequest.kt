package hu.bme.aut.android.gifthing.authentication.dto

class SignupRequest (val username: String,
                     val email: String,
                     val password: String,
                     val firstname: String?,
                     val lastname: String?
)
{
    private val role:HashSet<String> = HashSet()
    init {
        role.add("user")
    }
}
