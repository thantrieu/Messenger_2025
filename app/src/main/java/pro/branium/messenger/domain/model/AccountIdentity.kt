package pro.branium.messenger.domain.model

import pro.branium.messenger.domain.model.enums.AccountType

/**
 * Represents the core identity and authentication-related information for an account.
 * This data is fundamental for identifying and logging in a user.
 *
 * @property userId The unique identifier for the account (Primary Key).
 * @property username The unique username chosen by the user, potentially used for login.
 * @property email The user's unique email address, often used for login and communication.
 * @property accountType The type or tier of the account (e.g., FREE, PREMIUM, ADMIN).
 */
data class AccountIdentity(
    val userId: String,
    val username: String,
    val email: String,
    val accountType: AccountType
)
