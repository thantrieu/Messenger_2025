package pro.branium.messenger.domain.model.error

sealed class LogoutError(open val message: String) {
    data class ConnectivityIssue(override val message: String) : LogoutError(message)
    data class UnknownError(override val message: String) : LogoutError(message)
}