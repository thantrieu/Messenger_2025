package pro.branium.messenger.domain.usecase

import jakarta.inject.Inject
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.AccountRepository

class ResetPasswordUseCase @Inject constructor(private val repository: AccountRepository) {
    suspend fun execute(account: Account): Boolean {
        return repository.resetPassword(account)
    }
}