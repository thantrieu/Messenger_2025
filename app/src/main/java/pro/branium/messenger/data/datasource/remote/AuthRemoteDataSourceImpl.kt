package pro.branium.messenger.data.datasource.remote

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okio.IOException
import pro.branium.messenger.data.model.request.LoginRequest
import pro.branium.messenger.data.model.request.SignupRequest
import pro.branium.messenger.data.model.response.LoginResponse
import pro.branium.messenger.data.model.response.SignupResponse
import pro.branium.messenger.data.model.response.UserData
import pro.branium.messenger.domain.datasource.AuthRemoteDataSource
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.AuthError
import pro.branium.messenger.domain.model.error.LoginError
import pro.branium.messenger.domain.model.error.LogoutError
import pro.branium.messenger.domain.model.error.SignupError
import pro.branium.messenger.utils.Result
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions = Firebase.functions
) : AuthRemoteDataSource {
//    override suspend fun getProfile(username: String): Account? {
//        val retrofit = getAccountInfoRetrofit.create(AccountService::class.java)
//        val result = retrofit.getAccount(username)
//        return result.body()
//    }
//
//    override suspend fun checkEmail(email: String): Boolean {
//        val retrofit = checkEmailRetrofit.create(AccountService::class.java)
//        val result = retrofit.checkEmail(email)
//        return result.body() == true
//    }

    override suspend fun logout(refreshToken: String): Result<LogoutError, Unit> {
        return withContext(Dispatchers.IO) {
            val dataPayload = hashMapOf("refreshToken" to refreshToken)
            try {
                val result = functions
                    .getHttpsCallable("template-function")
                    .call(dataPayload)
                    .await()

                @Suppress("UNCHECKED_CAST")
                val responseData = result.data as? Map<String, Any>
                if (responseData == null) {
                    return@withContext Result.Failure(
                        LogoutError.UnknownError("Invalid response from server.")
                    )
                }

                val success = responseData["success"] as? Boolean == true
                if (success == true) {
                    Result.Success(Unit)
                } else {
                    val errorMessage =
                        responseData["message"] as? String ?: "Logout failed on server"
                    Result.Failure(LogoutError.UnknownError(errorMessage))
                }
            } catch (e: FirebaseFunctionsException) {
                val logoutError = mapFirebaseExceptionToLogoutError(e)
                Result.Failure(logoutError)
            } catch (e: IOException) {
                Result.Failure(LogoutError.ConnectivityIssue(e.message ?: "Network error occurred"))
            } catch (e: Exception) {
                Result.Failure(
                    LogoutError.UnknownError(
                        e.message ?: "An unknown error occurred"
                    )
                )
            }
        }
    }

    private fun mapFirebaseExceptionToLogoutError(e: FirebaseFunctionsException): LogoutError {
        return when (e.code) {
            FirebaseFunctionsException.Code.INVALID_ARGUMENT ->
                // This might happen if the refreshToken format was rejected by the backend validation
                LogoutError.UnknownError("Invalid argument sent for logout: ${e.message}")

            FirebaseFunctionsException.Code.INTERNAL ->
                LogoutError.UnknownError("Internal server error during logout: ${e.message}")

            FirebaseFunctionsException.Code.UNAVAILABLE ->
                LogoutError.ConnectivityIssue("Logout service unavailable: ${e.message}")
            // Other codes like UNAUTHENTICATED or PERMISSION_DENIED are less likely
            // for a simple token invalidation call, but could be added if needed.
            else -> LogoutError.UnknownError(
                e.message ?: "Unknown Firebase Functions error during logout."
            )
        }
    }

    override suspend fun signup(
        email: String,
        password: String,
        displayName: String,
        username: String,
        accountType: AccountType
    ): Result<SignupError, String> {
        return withContext(Dispatchers.IO) {
            val data = SignupRequest(
                email = email,
                password = password,
                displayName = displayName,
                username = username,
                accountType = accountType.name
            )

            try {
                val result = functions
                    .getHttpsCallable("template-function")
                    .call(data)
                    .await()
                val rawData = result.data
                if (rawData !is Map<*, *>) {
                    return@withContext Result.Failure(
                        SignupError.UnknownError("Invalid response structure from server.")
                    )
                }
                @Suppress("UNCHECKED_CAST")
                val resultMap = rawData as? Map<String, Any?>
                if (resultMap == null) {
                    return@withContext Result.Failure(
                        SignupError.UnknownError("Invalid response structure from server.")
                    )
                }
                val signupResponse = SignupResponse(
                    success = resultMap["success"] as? Boolean == true,
                    userId = resultMap["userId"] as? String,
                    error = resultMap["error"] as? String,
                    message = resultMap["message"] as? String
                )
                if (signupResponse.success == true && !signupResponse.userId.isNullOrBlank()) {
                    Result.Success(signupResponse.userId)
                } else {
                    val finalErrorMessage =
                        signupResponse.error ?: "Unknown signup error from server"
                    val signupError = mapServerMessageToSignupError(finalErrorMessage)
                    Result.Failure(signupError)
                }
            } catch (e: FirebaseFunctionsException) {
                val signupError = mapFirebaseExceptionToSignupError(e)
                Result.Failure(signupError)
            } catch (e: IOException) {
                Result.Failure(SignupError.ConnectivityIssue(e.message ?: "Network error occurred"))
            } catch (e: Exception) {
                Result.Failure(
                    SignupError.UnknownError(
                        e.message ?: "An unknown error occurred"
                    )
                )
            }
        }
    }

    // Helper function to map Firebase specific errors to Domain errors
    private fun mapFirebaseExceptionToSignupError(e: FirebaseFunctionsException): SignupError {
        return when (e.code) {
            FirebaseFunctionsException.Code.INVALID_ARGUMENT ->
                SignupError.InvalidInput(e.message ?: "Invalid input data sent.")

            FirebaseFunctionsException.Code.ALREADY_EXISTS ->
                SignupError.EmailAlreadyExists(
                    e.message ?: "Resource already exists (e.g., email)."
                )

            FirebaseFunctionsException.Code.UNAUTHENTICATED ->
                SignupError.UnknownError("Authentication issue during signup: ${e.message}")

            FirebaseFunctionsException.Code.PERMISSION_DENIED ->
                SignupError.UnknownError("Permission denied during signup: ${e.message}")

            FirebaseFunctionsException.Code.INTERNAL ->
                SignupError.UnknownError("Internal server error during signup: ${e.message}")

            FirebaseFunctionsException.Code.UNAVAILABLE ->
                SignupError.ConnectivityIssue("Signup service unavailable: ${e.message}")
            // Add mappings for other relevant codes if needed
            else -> SignupError.UnknownError(e.message ?: "Unknown Firebase Functions error.")
        }
    }

    // Helper function to map server's logical error message to Domain errors
    private fun mapServerMessageToSignupError(errorMessage: String): SignupError {
        return when {
            errorMessage.contains("already registered", ignoreCase = true) ||
                    errorMessage.contains("email already exists", ignoreCase = true) ->
                SignupError.EmailAlreadyExists(errorMessage)

            errorMessage.contains("invalid input", ignoreCase = true) -> // Example
                SignupError.InvalidInput(errorMessage)

            else -> SignupError.UnknownError(errorMessage)
        }
    }

    override suspend fun login(
        identity: String,
        password: String
    ): Result<LoginError, LoginResponse> {
        return withContext(Dispatchers.IO) {
            val data = LoginRequest(
                identity = identity,
                password = password
            )
            try {
                val result = functions
                    .getHttpsCallable("template-function")
                    .call(data)
                    .await()
                val rawData = result.data
                val loginResponse = mapRawDataToLoginResponse(rawData)
                if (loginResponse == null) {
                    return@withContext Result.Failure(
                        LoginError.Unknown("Invalid response structure from server.")
                    )
                }
                if (loginResponse.success == true && !loginResponse.token.isNullOrBlank()) {
                    Result.Success(loginResponse)
                } else {
                    val finalErrorMessage = loginResponse.message ?: loginResponse.error
                    ?: "Unknown login error from server"
                    val loginError = mapServerMessageToLoginError(finalErrorMessage)
                    Result.Failure(loginError)
                }
            } catch (e: FirebaseFunctionsException) {
                val loginError = mapFirebaseExceptionToLoginError(e)
                Result.Failure(loginError)
            } catch (e: IOException) {
                Result.Failure(LoginError.ConnectivityIssue(e.message ?: "Network error occurred"))
            } catch (e: Exception) {
                Result.Failure(
                    LoginError.Unknown(
                        e.message ?: "An unknown error occurred"
                    )
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapRawDataToLoginResponse(rawData: Any?): LoginResponse? {
        if (rawData !is Map<*, *>) {
            return null
        }
        val resultMap = rawData as? Map<String, Any?>
        if (resultMap == null) {
            return null
        }
        val loginResponse = LoginResponse(
            success = resultMap["success"] as? Boolean == true,
            message = resultMap["message"] as? String,
            error = resultMap["error"] as? String,
            token = resultMap["token"] as? String,
            expiresIn = resultMap["expiresIn"] as? Long,
            refreshToken = resultMap["refreshToken"] as? String,
            user = (resultMap["user"] as? Map<String, Any?>)?.let { userMap ->
                UserData(
                    userId = userMap["user"] as? String,
                    username = userMap["username"] as? String,
                    displayName = userMap["displayName"] as? String,
                    email = userMap["email"] as? String,
                    avatar = userMap["avatar"] as? String,
                    accountType = resultMap["accountType"] as? String,
                )
            }
        )
        return loginResponse
    }

    // Helper function to map Firebase specific errors to Domain errors
    private fun mapFirebaseExceptionToLoginError(e: FirebaseFunctionsException): LoginError {
        return when (e.code) {
            FirebaseFunctionsException.Code.INVALID_ARGUMENT ->
                LoginError.InvalidCredentials(
                    e.message ?: "Invalid credentials sent."
                ) // Map to InvalidCredentials
            FirebaseFunctionsException.Code.UNAUTHENTICATED -> // Might be used by backend for bad password
                LoginError.InvalidCredentials(e.message ?: "Invalid credentials.")

            FirebaseFunctionsException.Code.NOT_FOUND -> // Might be used by backend for user not found
                LoginError.InvalidCredentials(e.message ?: "User not found.")

            FirebaseFunctionsException.Code.INTERNAL ->
                LoginError.Unknown(e.message ?: "Internal server error during login.")

            FirebaseFunctionsException.Code.UNAVAILABLE ->
                LoginError.ConnectivityIssue("Login service unavailable: ${e.message}")
            // Add mappings for other relevant codes if needed
            else -> LoginError.Unknown(e.message ?: "Unknown Firebase Functions error.")
        }
    }

    // Helper function to map server's logical error message to Domain errors
    private fun mapServerMessageToLoginError(errorMessage: String): LoginError {
        return when {
            errorMessage.contains("invalid credential", ignoreCase = true) ||
                    errorMessage.contains("incorrect password", ignoreCase = true) ||
                    errorMessage.contains("user not found", ignoreCase = true) ->
                LoginError.InvalidCredentials(errorMessage)
            // Add more specific checks based on server error messages
            else -> LoginError.Unknown(errorMessage)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthError, LoginResponse> {
        return withContext(Dispatchers.IO) {
            // 1. Prepare the data payload
            val dataPayload = hashMapOf(
                "refreshToken" to refreshToken
            )
            try {
                // 2. Call the callable function
                val result = functions
                    .getHttpsCallable("template-function")
                    .call(dataPayload)
                    .await()

                // 3. Process the result data - Use helper to map Map to LoginResponse DTO
                val rawData: Any? = result.data

                // Use the same mapping helper as login, as the expected success structure is similar
                val loginResponse =
                    mapRawDataToLoginResponse(rawData) // Reuse or adapt mapping logic

                if (loginResponse == null) {
                    // Mapping failed or structure was invalid
                    return@withContext Result.Failure(
                        AuthError.Unknown("Invalid response structure from refresh token endpoint.")
                    )
                }

                // 4. Check for logical success/failure based on the LoginResponse DTO
                if (loginResponse.success == true && !loginResponse.token.isNullOrBlank()) {
                    // Logical success reported by the backend function
                    Result.Success(loginResponse) // Return the LoginResponse DTO with new tokens/data
                } else {
                    // Logical failure reported by the backend function
                    val finalErrorMessage = loginResponse.error
                        ?: loginResponse.message
                        ?: "Unknown token refresh error from server"
                    // Map the server's error message to a domain error
                    // Use AuthError or create a specific RefreshError type if needed
                    val authError = mapServerMessageToAuthError(finalErrorMessage)
                    Result.Failure(authError)
                }
            } catch (e: FirebaseFunctionsException) {
                // 5. Handle specific Firebase Functions exceptions
                val authError = mapFirebaseExceptionToAuthError(e) // Map to AuthError
                Result.Failure(authError)
            } catch (e: IOException) {
                // 6. Handle potential network errors
                Result.Failure(
                    AuthError.ConnectivityIssue(
                        e.message ?: "Network error occurred during refresh"
                    )
                )
            } catch (e: Exception) {
                // 7. Handle any other unexpected exceptions
                Result.Failure(
                    AuthError.Unknown(
                        e.message ?: "An unknown error occurred during refresh"
                    )
                )
            }
        }
    }

    // Helper function to map Firebase specific errors to Domain errors (AuthError)
    private fun mapFirebaseExceptionToAuthError(e: FirebaseFunctionsException): AuthError {
        return when (e.code) {
            FirebaseFunctionsException.Code.INVALID_ARGUMENT ->
                AuthError.Unknown(e.message ?: "Invalid argument for refresh.") // Or specific error
            FirebaseFunctionsException.Code.UNAUTHENTICATED -> // Backend might use this for invalid/expired token
                AuthError.SessionExpired(e.message ?: "Refresh token invalid or expired.")

            FirebaseFunctionsException.Code.NOT_FOUND -> // Backend might use this if token doc deleted
                AuthError.SessionExpired(e.message ?: "Refresh token not found.")

            FirebaseFunctionsException.Code.INTERNAL ->
                AuthError.Unknown(e.message ?: "Internal server error during refresh.")

            FirebaseFunctionsException.Code.UNAVAILABLE ->
                AuthError.ConnectivityIssue("Refresh service unavailable: ${e.message}")

            else -> AuthError.Unknown(
                e.message ?: "Unknown Firebase Functions error during refresh."
            )
        }
    }

    // Helper function to map server's logical error message to Domain errors (AuthError)
    private fun mapServerMessageToAuthError(errorMessage: String): AuthError {
        return when {
            // Check for specific messages indicating expired/invalid token
            errorMessage.contains("invalid", ignoreCase = true) ||
                    errorMessage.contains("expired", ignoreCase = true) ||
                    errorMessage.contains("not found", ignoreCase = true) ->
                AuthError.SessionExpired(errorMessage)
            // Add more specific checks if needed
            else -> AuthError.Unknown(errorMessage)
        }
    }
}