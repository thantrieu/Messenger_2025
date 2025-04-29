package pro.branium.messenger.data.model.request


data class SignupRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val username: String,
    val accountType: String
)