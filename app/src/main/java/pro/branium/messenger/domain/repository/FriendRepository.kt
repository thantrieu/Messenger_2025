package pro.branium.messenger.domain.repository

import pro.branium.messenger.domain.model.Account

interface FriendRepository {
    suspend fun addFriend(username: String, friendUsername: String): Boolean
    suspend fun unFriend(username: String, friendUsername: String): Boolean
    suspend fun block(username: String, friendUsername: String): Boolean
    suspend fun getFriends(username: String): List<Account>
    suspend fun getBlocked(username: String): List<Account>
}