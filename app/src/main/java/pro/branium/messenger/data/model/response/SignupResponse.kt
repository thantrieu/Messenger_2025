package pro.branium.messenger.data.model.response

data class SignupResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val userId: String? = null
)
