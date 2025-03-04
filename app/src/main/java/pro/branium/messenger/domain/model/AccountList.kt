package pro.branium.messenger.domain.model

import com.google.gson.annotations.SerializedName

data class AccountList(
    @SerializedName("accounts") val accounts: List<Account>
)
