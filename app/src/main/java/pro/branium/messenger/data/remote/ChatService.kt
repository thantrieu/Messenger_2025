package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatService {
    @Headers(
        "Content-Type: application/json"
    )
    @POST("/")
    suspend fun sendMessage(@Body message: Message): Response<ResponseResult>

    @GET("/")
    fun getChats(
        @Query("sender") sender: String,
        @Query("receiver") receiver: String
    ): Call<MessageList>

    @GET("/")
    fun getLastMessages(@Query("username") username: String): Call<MessageList>

    @GET("/")
    fun getMediaFiles(
        @Query("sender") sender: String,
        @Query("receiver") receiver: String
    ): Call<MessageList>
}