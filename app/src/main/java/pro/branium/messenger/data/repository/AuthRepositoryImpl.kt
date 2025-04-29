package pro.branium.messenger.data.repository

import pro.branium.messenger.data.model.response.UserData
import pro.branium.messenger.domain.datasource.AuthRemoteDataSource
import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.LoginError
import pro.branium.messenger.domain.model.error.LogoutError
import pro.branium.messenger.domain.model.error.SignupError
import pro.branium.messenger.domain.repository.AuthRepository
import pro.branium.messenger.domain.repository.TokenStorage
import pro.branium.messenger.utils.Result
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthRemoteDataSource,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override suspend fun login(
        identity: String,
        password: String
    ): Result<LoginError, AccountIdentity> {
        val loginDataSourceResult = dataSource.login(identity, password)
        return when (loginDataSourceResult) {
            is Result.Success -> {
                val loginResponse = loginDataSourceResult.value
                val accessToken = loginResponse.token
                val refreshToken = loginResponse.refreshToken
                val userData = loginResponse.user
                val userId = userData?.userId
                if (accessToken.isNullOrBlank() ||
                    refreshToken.isNullOrBlank() ||
                    userData == null ||
                    userId.isNullOrBlank()
                ) {
                    Result.Failure(LoginError.Unknown("Invalid login response from server"))
                } else {
                    try {
                        tokenStorage.saveAccessToken(accessToken)
                        tokenStorage.saveRefreshToken(refreshToken)
                        val accountIdentity = mapUserDataToAccountIdentity(userData)
                        if (accountIdentity == null) {
                            Result.Failure(LoginError.Unknown("Failed to process user data."))
                        } else {
                            Result.Success(accountIdentity)
                        }
                    } catch (e: Exception) {
                        Result.Failure(LoginError.Unknown("Failed to process login data locally"))
                    }
                }
            }

            is Result.Failure -> {
                Result.Failure(loginDataSourceResult.error)
            }
        }
    }

    private fun mapUserDataToAccountIdentity(data: UserData): AccountIdentity? {
        if (data.userId == null || data.email == null) {
            return null
        }
        return AccountIdentity(
            userId = data.userId,
            email = data.email,
            username = data.username ?: "",
            accountType = AccountType.valueOf(data.accountType ?: "FREE")
        )
    }

    override suspend fun logout(refreshToken: String): Result<LogoutError, Unit> {
        return dataSource.logout(refreshToken)
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String,
        username: String,
        accountType: AccountType
    ): Result<SignupError, String> {
        return dataSource.signup(email, password, displayName, username, accountType)
    }

//    override suspend fun updateAccount(account: Account): Boolean {
//        return dataSource.updateProfile(account)
//    }
//
//    override suspend fun deleteAccount(account: Account): Boolean {
//        return dataSource.deleteProfile(account)
//    }
//
//    override suspend fun forgotPassword(email: String): Boolean {
//        return dataSource.forgotPassword(email)
//    }
//
//    override suspend fun resetPassword(account: Account): Boolean {
//        return dataSource.resetPassword(account)
//    }
//
//    override suspend fun getAccountByUsername(username: String): Account? {
//        return dataSource.getProfile(username)
//    }
//
//    override suspend fun checkUsername(username: String): Boolean {
//        return dataSource.checkUsername(username)
//    }
//
//    override suspend fun checkEmail(email: String): Boolean {
//        return dataSource.checkEmail(email)
//    }
}