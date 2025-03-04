package pro.branium.messenger.domain.model

import android.net.Uri
import java.util.Date

data class Message(
    val id: Long,
    val sender: String,
    val senderDisplayName: String,
    val receiver: String,
    val data: Data,
    val notification: Notification,
    val timestamp: Long = Date().time,
    val status: MessageStatus,
    val token: String? = null
) {
    fun isIncoming(accUsername: String): Boolean {
        return accUsername.compareTo(sender) != 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false

        if (sender != other.sender) return false
        if (receiver != other.receiver) return false
        return timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = sender.hashCode()
        result = 31 * result + receiver.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

enum class MessageStatus {
    SENT,
    SEEN,
    SENDING,
    FAILED
}

data class Data(
    val text: String = "",
    // for image, video, audio, document
    val photoUrl: String? = null,
    val photoUri: Uri? = null,
    val photoMimeType: String? = null,

    val videoUrl: String? = null,
    val videoMimeType: String? = null,

    val audioUrl: String? = null,
    val audioMimeType: String? = null,

    val documentUrl: String? = null,
    val documentMimeType: String? = null
)

data class Notification(
    val title: String = "",
    val body: String? = null
)