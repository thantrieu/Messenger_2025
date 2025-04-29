package pro.branium.messenger.data.model.response

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("avatar")
    val avatar: String? = null,

    @SerializedName("accountType")
    val accountType: String? = null,
)