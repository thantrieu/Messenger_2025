package pro.branium.messenger.data.datasource.remote

import pro.branium.messenger.data.model.request.LoginRequest
import pro.branium.messenger.data.model.request.SignupRequest
import pro.branium.messenger.data.model.response.LoginResponse
import pro.branium.messenger.data.model.response.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/")
    suspend fun signup(@Body data: SignupRequest): Response<SignupResponse>

    @POST("/")
    suspend fun login(@Body data: LoginRequest): Response<LoginResponse>

    @POST("/")
    suspend fun logout(@Body userId: String): Response<Unit>
}