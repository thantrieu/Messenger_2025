package pro.branium.messenger.domain.model.error

sealed class ProfileError(open val message: String?) {
    data class NotFound(
        override val message: String? = "Profile not found"
    ) : ProfileError(message)

    data class NetworkError(
        override val message: String? = "Network error"
    ) : ProfileError(message)

    data class UpdateFailed(
        override val message: String?
    ) : ProfileError(message)

    data class Unknown(
        override val message: String? = "An unknown error occurred"
    ) : ProfileError(message)
}
