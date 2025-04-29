package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.SignupError
import pro.branium.messenger.domain.repository.AuthRepository
import pro.branium.messenger.utils.Result
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend fun execute(
        email: String,
        password: String,
        displayName: String,
        username: String,
        accountType: AccountType
    ): Result<SignupError, String> {
        return repository.signup(email, password, displayName, username, accountType)
    }
}