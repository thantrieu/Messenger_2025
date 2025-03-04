package pro.branium.messenger.domain.datasource

import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.model.Message

interface SearchingDataSource {
    suspend fun searchMessage(sender: String, receiver: String, query: String): List<Message>
    suspend fun searchFriend(query: String): List<Account>
}