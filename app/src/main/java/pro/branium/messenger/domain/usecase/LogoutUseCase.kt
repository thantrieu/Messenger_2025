package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun execute(refreshToken: String) = repository.logout(refreshToken)
}