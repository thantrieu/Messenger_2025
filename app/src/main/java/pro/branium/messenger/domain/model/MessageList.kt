package pro.branium.messenger.domain.model

import com.google.gson.annotations.SerializedName

data class MessageList(
    @SerializedName("messages") val messages: List<Message>
)
