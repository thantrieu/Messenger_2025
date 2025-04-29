package pro.branium.messenger.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import pro.branium.messenger.utils.AppUtils.stringToDate
import pro.branium.messenger.utils.AppUtils.toDateString
import java.time.Instant
import java.time.LocalDate
import java.util.Date
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Account(
    @SerializedName("userId")
    val userId: Long = -1,

    @SerializedName("username")
    var username: String = "",

    @SerializedName("passwordHash")
    var passwordHash: String = "",

    @SerializedName("email")
    var email: String = "",

    @SerializedName("phoneNumber")
    var phoneNumber: String = "",

    @SerializedName("displayName")
    var displayName: String = "",

    @SerializedName("birthDate")
    var birthDate: Date = Date(),

    @SerializedName("gender")
    var gender: String? = "",

    @SerializedName("occupation")
    var occupation: String? = "",

    @SerializedName("token")
    var token: String? = null,

    @SerializedName("avatar")
    var avatar: String? = null,

    @SerializedName("coverPhoto")
    var coverPhoto: String? = null,

    @SerializedName("slogan")
    var slogan: String = "No slogan",

    @SerializedName("createdAt")
    var createdAt: String = Date().toDateString(),

    @SerializedName("lastUpdated")
    var lastUpdated: String = Date().toDateString(),

    @SerializedName("accountType")
    var accountType: String = "default"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!.stringToDate(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(username)
        parcel.writeString(passwordHash)
        parcel.writeString(email)
        parcel.writeString(phoneNumber)
        parcel.writeString(displayName)
        parcel.writeString(birthDate.toDateString())
        parcel.writeString(gender)
        parcel.writeString(occupation)
        parcel.writeString(token)
        parcel.writeString(avatar)
        parcel.writeString(coverPhoto)
        parcel.writeString(slogan)
        parcel.writeString(createdAt)
        parcel.writeString(lastUpdated)
        parcel.writeString(accountType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account {
            return Account(parcel)
        }

        override fun newArray(size: Int): Array<Account?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Account) return false

        return username == other.username && userId == other.userId
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }
}

/**
 * Represents a record of an active user session on a specific device instance,
 * primarily used for managing FCM token associations in Firestore.
 * This corresponds to a document within the "userDevices" collection.
 *
 * IMPORTANT:
 * 1. Firestore Indexes: Ensure Firestore indexes are created on 'userId' and 'token' fields
 * in the 'userDevices' collection for efficient querying.
 * 2. userId Type: Note that 'userId' here is a String, intended to store the Firebase Auth UID
 * or a string representation of your internal Long userId. This differs from the Long type
 * used in AccountIdentity/UserProfile/AccountMetadata. You'll need to handle the conversion
 * (Long <-> String) in your application logic where these models interact.
 */
data class UserDevice(
    /**
     * The Firestore document ID (usually an Auto-ID).
     * Use @DocumentId annotation for Firestore to automatically populate this
     * field when reading the document. Nullable if the ID isn't always present
     * in the object instance (e.g., before saving).
     */
    @DocumentId
    val documentId: String? = null,

    /**
     * The unique identifier of the user. **Stored as a String** to align with Firebase Auth UID
     * standards or for general Firestore string-based querying.
     * Remember to convert your internal Long userId to String when writing this field,
     * and potentially back to Long when reading if needed for joining with other data.
     * Needs to be indexed in Firestore.
     */
    @get:PropertyName("userId") // Ensures correct mapping even if you rename the Kotlin property
    @set:PropertyName("userId")
    var userId: String = "", // Initialize to avoid null, Firestore prefers non-null where possible

    /**
     * The Firebase Cloud Messaging (FCM) registration token for this specific
     * app instance on this device.
     * Needs to be indexed in Firestore.
     */
    @get:PropertyName("token")
    @set:PropertyName("token")
    var token: String = "",

    /**
     * Timestamp indicating when this device/token was first registered for the user
     * during this session/installation. Set using FieldValue.serverTimestamp()
     * during creation in your backend logic. Nullable because it's generated by the server.
     * Note: @ServerTimestamp annotation requires specific rules/setup or is often handled manually.
     */
    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Timestamp? = null, // Use com.google.firebase.Timestamp

    /**
     * Timestamp indicating the last time this token was confirmed active (e.g., on login
     * or token refresh). Updated using FieldValue.serverTimestamp() in backend logic. Nullable.
     */
    @get:PropertyName("lastLogin")
    @set:PropertyName("lastLogin")
    var lastLogin: Timestamp? = null, // Use com.google.firebase.Timestamp

    /**
     * Optional: The platform the token belongs to (e.g., "android", "ios", "web").
     */
    @get:PropertyName("platform")
    @set:PropertyName("platform")
    var platform: String? = null,

    /**
     * Optional: A unique identifier for the physical device, if available and needed
     * for specific logic (e.g., limiting sessions per physical device).
     */
    @get:PropertyName("deviceId")
    @set:PropertyName("deviceId")
    var deviceId: String? = null
) {
    // Firestore often requires a no-argument constructor for deserialization.
    // Data classes usually generate one implicitly, but it can be added explicitly if needed:
    constructor() : this(documentId = null, userId = "", token = "", createdAt = null, lastLogin = null, platform = null, deviceId = null)
}