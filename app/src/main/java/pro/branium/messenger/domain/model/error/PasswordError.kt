package pro.branium.messenger.domain.model.error

sealed class PasswordError(open val message: String?) {
    data class EmailNotFound(override val message: String? = "Email address not found") :
        PasswordError(message)

    data class ForgotPasswordRateLimit(override val message: String? = "Try again later") :
        PasswordError(message)

    // Reset Password Errors
    data class InvalidResetToken(
        override val message: String? = "Password reset link is invalid or expired"
    ) : PasswordError(message)

    data class WeakPassword(
        override val message: String? = "Password does not meet requirements"
    ) : PasswordError(message)

    // Common Errors
    data class NetworkError(override val message: String? = "Network error occurred") :
        PasswordError(message)

    data class Unknown(override val message: String? = "An unknown error occurred") :
        PasswordError(message)
}