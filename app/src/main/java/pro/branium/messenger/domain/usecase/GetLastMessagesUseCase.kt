package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.MessageList
import pro.branium.messenger.domain.repository.ChatRepository
import javax.inject.Inject

class GetLastMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend fun execute(username: String): Result<MessageList> =
        repository.getAllLastMessage(username)
}