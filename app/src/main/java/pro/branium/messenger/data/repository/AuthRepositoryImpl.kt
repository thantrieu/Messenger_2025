package pro.branium.messenger.data.repository

import pro.branium.messenger.data.model.response.UserData
import pro.branium.messenger.domain.datasource.AuthRemoteDataSource
import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.AuthError
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
        password: String,
        rememberMe: Boolean
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
                        if (rememberMe) {
                            tokenStorage.saveRefreshToken(refreshToken)
                        }
                        val accountIdentity = mapUserDataToAccountIdentity(userData)
                        if (accountIdentity == null) {
                            Result.Failure(LoginError.Unknown("Failed to process user data."))
                        } else {
                            Result.Success(accountIdentity)
                        }
                    } catch (e: Exception) {
                        Result.Failure(
                            LoginError.Unknown(
                                e.message ?: "Failed to process login data locally"
                            )
                        )
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

    override suspend fun logout(): Result<LogoutError, Unit> {
        try {
            val currentRefreshToken = tokenStorage.getRefreshToken()
            if (!currentRefreshToken.isNullOrBlank()) {
                val remoteResult = dataSource.logout(currentRefreshToken)
                if (remoteResult is Result.Failure) {
                    return Result.Failure(remoteResult.error)
                }
            }
            tokenStorage.clearTokens()
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(LogoutError.UnknownError(e.message ?: "Unknown error"))
        }
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


    override suspend fun checkSession(): Result<AuthError, AccountIdentity> {
        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            // No persistent token, definitely not logged in
            return Result.Failure(AuthError.NotLoggedIn())
        }

        // We have a refresh token, try to use it to get a new access token
        // This validates the refresh token and confirms the session
        val refreshResult = dataSource.refreshToken(refreshToken)

        return when (refreshResult) {
            is Result.Success -> {
                // Refresh successful! Session is valid.
                val loginResponse = refreshResult.value // Contains new tokens and user data
                val newAccessToken = loginResponse.token
                val newRefreshToken = loginResponse.refreshToken // Backend should rotate tokens
                val userData = loginResponse.user
                val userId = userData?.userId

                // Validate response from refresh endpoint
                if (newAccessToken.isNullOrBlank() || newRefreshToken.isNullOrBlank() || userData == null || userId.isNullOrBlank()) {
                    // If refresh succeeded but response is invalid, treat as session expired/invalid
                    tokenStorage.clearTokens() // Clear invalid local state
                    Result.Failure(AuthError.SessionExpired("Invalid refresh response from server."))
                } else {
                    try {
                        // Save the NEW tokens
                        tokenStorage.saveAccessToken(newAccessToken)
                        tokenStorage.saveRefreshToken(newRefreshToken) // Save the rotated token

                        // Map UserData DTO to AccountIdentity Domain Entity
                        val accountIdentity = mapUserDataToAccountIdentity(userData)

                        if (accountIdentity == null) {
                            tokenStorage.clearTokens() // Clear tokens if mapping fails
                            Result.Failure(AuthError.Unknown("Failed to process user data after refresh."))
                        } else {
                            Result.Success(accountIdentity) // Return the Domain Entity
                        }
                    } catch (e: Exception) {
                        Result.Failure(AuthError.Unknown("Failed to process session data locally after refresh."))
                    }
                }
            }
            is Result.Failure -> {
                // Refresh failed (e.g., token expired, invalid, network error)
                val authError = refreshResult.error
                // Clear potentially invalid/expired local tokens
                try {
                    tokenStorage.clearTokens()
                } catch (e: Exception) {
                    // Log error but proceed to return session expired/failure
                }
                // Return a specific error indicating the session is no longer valid
                Result.Failure(AuthError.SessionExpired(authError.message ?: "Session is invalid or expired."))
            }
        }
    }
}