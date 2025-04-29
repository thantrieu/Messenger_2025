package pro.branium.messenger.domain.datasource

import pro.branium.messenger.domain.model.error.IdentityCheckError
import pro.branium.messenger.utils.Result

interface IdentityValidationRemoteDataSource {
    suspend fun checkUsername(username: String): Result<IdentityCheckError, Boolean>
    suspend fun checkEmail(email: String): Result<IdentityCheckError, Boolean>
}