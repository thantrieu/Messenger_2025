package pro.branium.messenger.data.datasource.remote

import pro.branium.messenger.domain.datasource.FriendDataSource
import pro.branium.messenger.domain.model.Account
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class FriendDataSourceImpl @Inject constructor(
    @Named("getAllFriends") private val getAllFriendsRetrofit: Retrofit,
) : FriendDataSource {
    override suspend fun addFriend(username: String, friendUsername: String): Boolean {
        // todo
        return false
    }

    override suspend fun unFriend(username: String, friendUsername: String): Boolean {
        // todo
        return false
    }

    override suspend fun block(username: String, friendUsername: String): Boolean {
        // todo
        return false
    }

    override suspend fun getFriends(username: String): List<Account> {
        val retrofit = getAllFriendsRetrofit.create(FriendService::class.java)
        val result = retrofit.getFriendAccounts(username)
        if (result.isSuccessful) {
            return result.body() ?: emptyList()
        }
        return emptyList()
    }

    override suspend fun getBlocked(username: String): List<Account> {
        // todo
        return emptyList()
    }
}