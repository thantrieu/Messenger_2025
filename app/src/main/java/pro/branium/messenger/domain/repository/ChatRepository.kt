package pro.branium.messenger.domain.repository

import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList

interface ChatRepository {
    suspend fun sendMessage(message: Message): Boolean
    suspend fun getChat(sender: String, receiver: String): Result<MessageList>
    suspend fun getAllLastMessage(username: String): Result<MessageList>
    suspend fun getAllMediaFile(username: String, receiver: String): Result<MessageList>
}