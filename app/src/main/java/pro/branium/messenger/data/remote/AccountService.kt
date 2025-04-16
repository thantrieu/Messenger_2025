package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.model.Account
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AccountService {
    @GET("/")
    suspend fun getAccount(@Query("username") username: String): Response<Account?>

    @GET("/")
    suspend fun checkEmail(@Query("email") email: String): Response<Boolean>

    @POST("/messenger")
    suspend fun createAccount(@Body account: Account): Response<ResponseResult>

    @POST("/")
    suspend fun updateAccount(@Body account: Account): Response<ResponseResult>

    @DELETE("/")
    suspend fun deleteAccount(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<ResponseResult>

    @POST("/")
    suspend fun login(@Body account: Account): Response<Account?>
}