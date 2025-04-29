package pro.branium.messenger.domain.model

import pro.branium.messenger.domain.model.enums.Gender
import java.time.LocalDate

/**
 * Represents the user-visible profile information associated with an account.
 * These details are often provided by the user and displayed on their profile page.
 *
 * @property userId The unique identifier for the account (Foreign Key linking to AccountIdentity).
 * @property displayName The name displayed publicly for the user (might differ from username). Nullable if optional.
 * @property phoneNumber The user's phone number. Nullable if optional.
 * @property birthDate The user's date of birth. Nullable if optional. Consider using LocalDate.
 * @property gender The user's gender identity. Nullable if optional. Consider using an Enum.
 * @property occupation The user's occupation. Nullable if optional.
 * @property avatar A URL or reference path to the user's profile picture. Nullable if optional.
 * @property coverPhoto A URL or reference path to the user's profile cover photo. Nullable if optional.
 * @property slogan A short bio or tagline provided by the user. Nullable if optional.
 */
data class UserProfile(
    val userId: String,
    val displayName: String?,
    val phoneNumber: String?,
    val birthDate: LocalDate?,
    val gender: Gender?,
    val occupation: String?,
    val avatar: String?,
    val coverPhoto: String?,
    val slogan: String?
)
