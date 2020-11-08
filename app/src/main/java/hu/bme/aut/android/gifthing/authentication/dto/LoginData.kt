package hu.bme.aut.android.gifthing.authentication.dto

class LoginData(
    var accessToken: String = "",
    var id: Long = -1,
    var username: String? = null,
    var email: String? = null,
    var roles: Set<String> = HashSet()
)