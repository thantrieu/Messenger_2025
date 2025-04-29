package pro.branium.messenger.data.model.request

data class LoginRequest(
    val identity: String,
    val password: String
)
