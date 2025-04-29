package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.PasswordRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: PasswordRepository
) {
    suspend fun execute(email: String) = authRepository.forgotPassword(email)
}