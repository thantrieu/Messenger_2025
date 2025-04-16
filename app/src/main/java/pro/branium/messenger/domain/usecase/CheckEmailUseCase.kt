package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.AccountRepository
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend fun execute(email: String) = repository.checkEmail(email)
}