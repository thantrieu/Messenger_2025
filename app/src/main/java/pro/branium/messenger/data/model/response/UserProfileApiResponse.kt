package pro.branium.messenger.data.model.response

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class UserProfileApiResponse(
    @get:PropertyName("displayName") @set:PropertyName("displayName")
    var displayName: String? = null, // Use var if needed for Firestore deserialization
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber")
    var phoneNumber: String? = null,
    @get:PropertyName("birthDate") @set:PropertyName("birthDate")
    var birthDate: Timestamp? = null, // Firestore Timestamp
    @get:PropertyName("gender") @set:PropertyName("gender")
    var gender: String? = null, // Stored as String
    @get:PropertyName("occupation") @set:PropertyName("occupation")
    var occupation: String? = null,
    @get:PropertyName("avatar") @set:PropertyName("avatar")
    var avatar: String? = null,
    @get:PropertyName("coverPhoto") @set:PropertyName("coverPhoto")
    var coverPhoto: String? = null,
    @get:PropertyName("slogan") @set:PropertyName("slogan")
    var slogan: String? = null
) {
    constructor(): this(null, null, null, null, null, null, null, null)
}