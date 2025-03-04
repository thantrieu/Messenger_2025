package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.model.Account
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FriendService {
    @GET("/")
    suspend fun getFriendAccounts(@Query("username") username: String): Response<List<Account>>
}