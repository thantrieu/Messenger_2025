package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.repository.ProfileRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(userId: String) = repository.deleteProfile(userId)
}