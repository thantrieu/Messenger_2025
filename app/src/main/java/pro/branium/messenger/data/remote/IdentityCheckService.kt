package pro.branium.messenger.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IdentityCheckService {
    @GET("/")
    suspend fun checkEmail(@Query("email") email: String): Response<Boolean>
}