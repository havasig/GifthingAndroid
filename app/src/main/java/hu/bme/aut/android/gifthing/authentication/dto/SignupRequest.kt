package hu.bme.aut.android.gifthing.services

class SignupRequest (val username: String,
                     val email: String,
                     val password: String)
{
    private val role:HashSet<String> = HashSet()
    init {
        role.add("user")
    }
}
