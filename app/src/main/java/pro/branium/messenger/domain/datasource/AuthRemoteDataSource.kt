package pro.branium.messenger.domain.datasource

import pro.branium.messenger.data.model.response.LoginResponse
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.LoginError
import pro.branium.messenger.domain.model.error.LogoutError
import pro.branium.messenger.domain.model.error.SignupError
import pro.branium.messenger.utils.Result

interface AuthRemoteDataSource {
    suspend fun login(identity: String, password: String): Result<LoginError, LoginResponse>
    suspend fun logout(refreshToken: String): Result<LogoutError, Unit>
    suspend fun signup(
        email: String,
        password: String,
        displayName: String,
        username: String,
        accountType: AccountType
    ): Result<SignupError, String>
}