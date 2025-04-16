package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.AccountRepository
import pro.branium.messenger.presentation.screens.SignupState
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend fun execute(account: Account): SignupState = repository.signup(account)
}