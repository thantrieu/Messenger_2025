package pro.branium.messenger.domain.usecase

import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.FriendRepository
import javax.inject.Inject

class GetFriendAccountsUseCase @Inject constructor(
    private val repository: FriendRepository
) {
    suspend fun execute(username: String): List<Account> = repository.getFriends(username)
}