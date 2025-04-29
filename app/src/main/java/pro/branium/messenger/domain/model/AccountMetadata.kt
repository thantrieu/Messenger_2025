package pro.branium.messenger.domain.model

import java.time.Instant

/**
 * Represents system-managed metadata about the account record itself.
 * This data is typically generated and updated automatically by the system.
 *
 * @property userId The unique identifier for the account (Foreign Key linking to AccountIdentity).
 * @property createdAt Timestamp indicating when the account was created. Consider using Instant or Long.
 * @property lastUpdated Timestamp indicating the last time the account or profile data was modified. Consider using Instant or Long.
 */
data class AccountMetadata(
    val userId: String,
    val createdAt: Instant,
    val lastUpdated: Instant
)
