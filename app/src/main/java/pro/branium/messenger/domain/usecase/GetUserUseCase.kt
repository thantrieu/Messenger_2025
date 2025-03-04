package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.AccountRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repository: AccountRepository) {
    suspend fun execute(username: String) = repository.getAccountByUsername(username)
}