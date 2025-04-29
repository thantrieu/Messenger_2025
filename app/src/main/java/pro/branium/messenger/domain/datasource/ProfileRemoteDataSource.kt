package pro.branium.messenger.domain.datasource

import pro.branium.messenger.data.model.response.UserProfileApiResponse
import pro.branium.messenger.domain.model.error.ProfileError
import pro.branium.messenger.utils.Result

interface ProfileRemoteDataSource {
    suspend fun getProfile(userId: String): Result<ProfileError, UserProfileApiResponse>
    suspend fun updateProfile(userId: String, data: Map<String, Any?>): Result<ProfileError, Unit>
    suspend fun deleteProfile(userId: String): Result<ProfileError, Unit>
}