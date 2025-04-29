package pro.branium.messenger.domain.usecase

import jakarta.inject.Inject
import pro.branium.messenger.domain.repository.PasswordRepository

class ResetPasswordUseCase @Inject constructor(
    private val repository: PasswordRepository
) {
    suspend fun execute(token: String, newPassword: String): Boolean {
        return repository.resetPassword(token, newPassword)
    }
}