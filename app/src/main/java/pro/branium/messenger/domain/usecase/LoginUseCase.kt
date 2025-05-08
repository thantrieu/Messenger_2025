package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.error.LoginError
import pro.branium.messenger.domain.repository.AuthRepository
import pro.branium.messenger.utils.Result
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun execute(
        identity: String,
        password: String,
        rememberMe: Boolean
    ): Result<LoginError, AccountIdentity> {
        return repository.login(identity, password, rememberMe)
    }
}