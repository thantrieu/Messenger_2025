package pro.branium.messenger.domain.repository

import pro.branium.messenger.domain.model.UserProfile
import pro.branium.messenger.domain.model.error.ProfileError
import pro.branium.messenger.utils.Result

interface ProfileRepository {
    suspend fun getProfile(userId: String): Result<ProfileError, UserProfile>
    suspend fun updateProfile(userId: String, data: UserProfile): Result<ProfileError, Unit>
    suspend fun deleteProfile(userId: String): Result<ProfileError, Unit>
}