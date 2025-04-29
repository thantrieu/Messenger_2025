package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.IdentityValidationRepository
import javax.inject.Inject

class CheckEmailUseCase @Inject constructor(
    private val repository: IdentityValidationRepository
) {
    suspend fun execute(email: String) = repository.checkEmail(email)
}