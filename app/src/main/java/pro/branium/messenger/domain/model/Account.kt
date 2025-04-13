package pro.branium.messenger.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import pro.branium.messenger.utils.AppUtils.toDateString
import java.util.Date

data class Account(
    @SerializedName("username")
    var username: String = "",

    @SerializedName("password")
    var password: String = "",

    @SerializedName("email")
    var email: String = "",

    @SerializedName("phoneNumber")
    var phoneNumber: String = "",

    @SerializedName("displayName")
    var displayName: String = "",

    @SerializedName("gender")
    var gender: String? = "",

    @SerializedName("occupation")
    var occupation: String? = "",

    @SerializedName("token")
    var token: String? = null,

    @SerializedName("imageUrl")
    var imageUrl: String? = null,

    @SerializedName("slogan")
    var slogan: String = "No slogan",

    @SerializedName("createdAt")
    var createdAt: String = Date().toDateString(),

    @SerializedName("friends")
    var friends: MutableList<String> = mutableListOf(),

    @SerializedName("accountType")
    var accountType: String = "default"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(email)
        parcel.writeString(displayName)
        parcel.writeString(gender)
        parcel.writeString(token)
        parcel.writeString(imageUrl)
        parcel.writeString(slogan)
        parcel.writeString(createdAt)
        parcel.writeStringList(friends)
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

        return username == other.username
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }

    override fun toString(): String {
        return "Account(username='$username', password='$password', " +
                "email='$email', displayName='$displayName', gender=$gender, " +
                "token=$token, imageUrl=$imageUrl, createdAt=$createdAt, friends=$friends)"
    }
}