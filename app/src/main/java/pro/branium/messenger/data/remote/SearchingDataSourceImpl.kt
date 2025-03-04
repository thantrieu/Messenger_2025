package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.datasource.SearchingDataSource
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.model.Message
import javax.inject.Inject

class SearchingDataSourceImpl @Inject constructor() : SearchingDataSource {
    override suspend fun searchMessage(
        sender: String,
        receiver: String,
        query: String
    ): List<Message> {
        // todo
        return emptyList()
    }

    override suspend fun searchFriend(query: String): List<Account> {
        // todo
        return emptyList()
    }
}