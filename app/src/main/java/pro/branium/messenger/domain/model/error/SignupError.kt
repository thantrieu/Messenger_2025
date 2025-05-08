package pro.branium.messenger.domain.model.error

sealed class SignupError(open val message: String?) {
    data class EmailAlreadyExists(
        override val message: String? = "Email already exists"
    ) : SignupError(message)

    data class InvalidInput(
        override val message: String? = "Invalid input"
    ) : SignupError(message)

    data class ConnectivityIssue(
        override val message: String? = "Could not connect to service to process signup operation."
    ) : SignupError(message)

    data class UnknownError(
        override val message: String? = "An unknown error occurred"
    ) : SignupError(message)
}