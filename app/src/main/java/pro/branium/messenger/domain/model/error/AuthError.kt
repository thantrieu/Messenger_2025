package pro.branium.messenger.domain.model.error

sealed class AuthError(open val message: String?) {
    data class NotLoggedIn(
        override val message: String? = "No active session found."
    ) : AuthError(message)

    data class SessionExpired(
        override val message: String? = "Session expired. Please log in again."
    ) : AuthError(message)

    data class NetworkError(
        override val message: String? = "Network error during session check."
    ) : AuthError(message)

    data class Unknown(
        override val message: String? = "An unknown error occurred checking session."
    ) : AuthError(message)
}