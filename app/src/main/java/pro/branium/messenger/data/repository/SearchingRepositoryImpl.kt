package pro.branium.messenger.data.repository

import pro.branium.messenger.domain.datasource.SearchingDataSource
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.repository.SearchingRepository
import javax.inject.Inject

class SearchingRepositoryImpl @Inject constructor(
    private val dataSource: SearchingDataSource
) : SearchingRepository {
    override suspend fun searchMessage(
        sender: String,
        receiver: String,
        query: String
    ): List<Message> {
        return dataSource.searchMessage(sender, receiver, query)
    }

    override suspend fun searchFriend(query: String): List<Account> {
        return dataSource.searchFriend(query)
    }
}