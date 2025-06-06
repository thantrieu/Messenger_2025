package pro.branium.messenger.domain.model.error

sealed class LoginError(open val message: String?) {

    data class InvalidCredentials(
        override val message: String? = "Invalid identifier or password."
    ) : LoginError(message)

    /**
     * Represents errors related to network connectivity during the login attempt
     * (e.g., no internet, server unavailable).
     */
    data class ConnectivityIssue(
        override val message: String? = "Could not connect to service to process login operation."
    ) : LoginError(message)

    /**
     * Represents unexpected errors that occurred during the login process,
     * either on the client or the server.
     */
    data class Unknown(
        override val message: String? = "An unknown login error occurred."
    ) : LoginError(message)
}