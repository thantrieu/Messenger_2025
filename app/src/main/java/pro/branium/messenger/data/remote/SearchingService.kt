package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.model.Message
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchingService {
    @GET("/")
    suspend fun searchMessage(
        @Query("sender") sender: String,
        @Query("receiver") receiver: String,
        @Query("query") query: String
    ): Response<List<Message>>

    @GET("/")
    suspend fun searchFriend(@Query("query") query: String): Response<List<Account>>
}