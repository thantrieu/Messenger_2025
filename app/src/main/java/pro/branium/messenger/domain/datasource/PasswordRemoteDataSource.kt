package pro.branium.messenger.domain.datasource

import pro.branium.messenger.domain.model.error.PasswordError
import pro.branium.messenger.utils.Result

interface PasswordRemoteDataSource {
    suspend fun forgotPassword(email: String): Result<PasswordError, Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<PasswordError, Unit>
}