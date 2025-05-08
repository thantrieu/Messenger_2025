package pro.branium.messenger.domain.model.error

sealed class IdentityCheckError(open val message: String?) {
    data class ConnectivityIssue(
        override val message: String? =
            "Could not connect to service to process identity check operation."
    ) : IdentityCheckError(message)

    data class Unknown(
        override val message: String? = "An unknown error occurred during check"
    ) : IdentityCheckError(message)
}