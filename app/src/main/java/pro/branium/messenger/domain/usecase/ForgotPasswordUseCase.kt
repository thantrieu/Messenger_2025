package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.AccountRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend fun execute(email: String) = accountRepository.forgotPassword(email)
}