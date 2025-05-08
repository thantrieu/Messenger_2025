package pro.branium.messenger.domain.repository

import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.AuthError
import pro.branium.messenger.domain.model.error.LoginError
import pro.branium.messenger.domain.model.error.LogoutError
import pro.branium.messenger.domain.model.error.SignupError
import pro.branium.messenger.utils.Result

interface AuthRepository {
    suspend fun login(
        identity: String,
        password: String,
        rememberMe: Boolean
    ): Result<LoginError, AccountIdentity>

    suspend fun logout(): Result<LogoutError, Unit>

    suspend fun signup(
        email: String,
        password: String,
        displayName: String,
        username: String,
        accountType: AccountType
    ): Result<SignupError, String>

    suspend fun checkSession(): Result<AuthError, AccountIdentity>
}