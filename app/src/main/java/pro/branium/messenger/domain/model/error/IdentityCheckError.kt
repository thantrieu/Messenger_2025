package pro.branium.messenger.domain.model.error

sealed class IdentityCheckError(open val message: String?) {
    data class NetworkError(override val message: String? = "Network error occurred") :
        IdentityCheckError(message)

    data class Unknown(override val message: String? = "An unknown error occurred during check") :
        IdentityCheckError(message)
}