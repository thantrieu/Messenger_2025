package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.datasource.ChatDataSource
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatDataSourceImpl @Inject constructor(
    @Named("sendMessage") private val sendMessageRetrofit: Retrofit,
    @Named("getChat") private val getChatRetrofit: Retrofit,
    @Named("getAllLastMessages") private val getAllLastMessagesRetrofit: Retrofit
) : ChatDataSource {
    override suspend fun sendMessage(message: Message): Boolean {
        val retrofit = sendMessageRetrofit.create(ChatService::class.java)
        val result = retrofit.sendMessage(message)
        if (result.isSuccessful) {
            return result.body()?.success ?: false
        }
        return false
    }

    override suspend fun getChats(sender: String, receiver: String): Result<MessageList> {
        return suspendCoroutine { continuation ->
            val retrofit = getChatRetrofit.create(ChatService::class.java)
            val result = retrofit.getChats(sender, receiver)
            result.enqueue(object : Callback<MessageList> {
                override fun onResponse(call: Call<MessageList>, response: Response<MessageList>) {
                    if (response.isSuccessful && response.body() != null) {
                        val messages = response.body()?.messages ?: emptyList()
                        val messageList = MessageList(messages)
                        continuation.resume(Result.success(messageList))
                    } else {
                        continuation.resume(Result.failure(Exception("Something went wrong")))
                    }
                }

                override fun onFailure(p0: Call<MessageList>, throwable: Throwable) {
                    continuation.resume(Result.failure(Exception(throwable.message)))
                }
            })
        }
    }

    override suspend fun getAllLastMessage(username: String): Result<MessageList> {
        val retrofit = getAllLastMessagesRetrofit.create(ChatService::class.java)
        val resultCall = retrofit.getLastMessages(username)
        return suspendCoroutine { continuation ->
            resultCall.enqueue(object : Callback<MessageList> {
                override fun onResponse(p0: Call<MessageList>, response: Response<MessageList>) {
                    if (response.isSuccessful && response.body() != null) {
                        val messages = response.body()?.messages ?: emptyList()
                        val messageList = MessageList(messages)
                        continuation.resume(Result.success(messageList))
                    } else {
                        continuation.resume(Result.failure(Exception("Something went wrong")))
                    }
                }

                override fun onFailure(p0: Call<MessageList>, t: Throwable) {
                    continuation.resume(Result.failure(Exception(t.message)))
                }
            })
        }
    }

    override suspend fun getAllMediaFile(sender: String, receiver: String): Result<MessageList> {
        // todo
        return Result.success(MessageList(emptyList()))
    }
}