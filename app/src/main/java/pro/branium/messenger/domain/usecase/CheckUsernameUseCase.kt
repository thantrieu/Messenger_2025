package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.AccountRepository
import javax.inject.Inject

class CheckUsernameUseCase @Inject constructor(private val repository: AccountRepository) {
    suspend fun execute(username: String): Boolean {
        return repository.checkUsername(username)
    }
}