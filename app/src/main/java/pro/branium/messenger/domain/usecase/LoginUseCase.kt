package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.AccountRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend fun execute(account: Account) = repository.login(account)
}