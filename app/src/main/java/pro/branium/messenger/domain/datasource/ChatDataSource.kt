package pro.branium.messenger.domain.datasource

import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList

interface ChatDataSource {
    suspend fun sendMessage(message: Message): Boolean
    suspend fun getChats(sender: String, receiver: String): Result<MessageList>
    suspend fun getAllLastMessage(username: String): Result<MessageList>
    suspend fun getAllMediaFile(sender: String, receiver: String): Result<MessageList>
}