package pro.branium.messenger.domain.repository

interface IdentityValidationRepository {
    suspend fun checkUsername(username: String): Boolean
    suspend fun checkEmail(email: String): Boolean
}