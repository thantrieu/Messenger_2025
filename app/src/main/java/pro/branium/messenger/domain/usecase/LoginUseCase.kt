package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun execute(identity: String, password: String) = repository.login(identity, password)
}