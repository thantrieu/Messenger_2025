package pro.branium.messenger.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("expiresIn")
    val expiresIn: Long? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("accountType")
    val accountType: String? = null,

    @SerializedName("user")
    val user: UserData? = null
)