package pro.branium.messenger.domain.model.error

sealed class SignupError(open val message: String?) {
    data class EmailAlreadyExists(
        override val message: String? = "Email already exists"
    ) : SignupError(message)

    data class InvalidInput(
        override val message: String? = "Invalid input"
    ) : SignupError(message)

    data class NetworkError(
        override val message: String? = "Network error occurred"
    ) : SignupError(message)

    data class UnknownError(
        override val message: String? = "An unknown error occurred"
    ) : SignupError(message)
}