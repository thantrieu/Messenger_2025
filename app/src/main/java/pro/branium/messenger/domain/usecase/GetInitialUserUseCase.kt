package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.error.AuthError
import pro.branium.messenger.domain.repository.AuthRepository
import pro.branium.messenger.utils.Result
import javax.inject.Inject

class GetInitialUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun execute(): Result<AuthError, AccountIdentity> {
        return repository.checkSession()
    }
}