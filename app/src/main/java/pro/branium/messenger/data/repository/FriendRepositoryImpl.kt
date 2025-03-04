package pro.branium.messenger.data.repository

import pro.branium.messenger.domain.datasource.FriendDataSource
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val dataSource: FriendDataSource
) : FriendRepository {
    override suspend fun addFriend(username: String, friendUsername: String): Boolean {
        return dataSource.addFriend(username, friendUsername)
    }

    override suspend fun unFriend(username: String, friendUsername: String): Boolean {
        return dataSource.unFriend(username, friendUsername)
    }

    override suspend fun block(username: String, friendUsername: String): Boolean {
        return dataSource.block(username, friendUsername)
    }

    override suspend fun getFriends(username: String): List<Account> {
        return dataSource.getFriends(username)
    }

    override suspend fun getBlocked(username: String): List<Account> {
        return dataSource.getBlocked(username)
    }
}