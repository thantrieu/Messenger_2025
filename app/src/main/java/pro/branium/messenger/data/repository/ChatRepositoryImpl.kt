package pro.branium.messenger.data.repository

import pro.branium.messenger.domain.datasource.ChatDataSource
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList
import pro.branium.messenger.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dataSource: ChatDataSource
) : ChatRepository {
    override suspend fun sendMessage(message: Message): Boolean {
        return dataSource.sendMessage(message)
    }

    override suspend fun getChat(sender: String, receiver: String): Result<MessageList> {
        return dataSource.getChats(sender, receiver)
    }

    override suspend fun getAllLastMessage(username: String): Result<MessageList> {
        return dataSource.getAllLastMessage(username)
    }

    override suspend fun getAllMediaFile(username: String, receiver: String): Result<MessageList> {
        return dataSource.getAllMediaFile(username, receiver)
    }
}