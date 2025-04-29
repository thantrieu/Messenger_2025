package pro.branium.messenger.domain.repository

interface PasswordRepository {
    suspend fun forgotPassword(email: String): Boolean
    suspend fun resetPassword(token: String, newPassword: String): Boolean
}