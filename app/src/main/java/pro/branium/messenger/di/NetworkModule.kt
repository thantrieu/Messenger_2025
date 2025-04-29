package pro.branium.messenger.di

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.utils.CloudFunctionUrlBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Named
import javax.inject.Singleton
import pro.branium.messenger.utils.CloudFunctionNames
import androidx.core.net.toUri

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideUriTypeAdapter(): Any {
        return object : JsonDeserializer<Uri>, JsonSerializer<Uri> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Uri {
                return json?.asString.toString().toUri()
            }

            override fun serialize(
                src: Uri?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement {
                return JsonPrimitive(src.toString())
            }
        }
    }

    @Provides
    @Singleton
    fun provideGson(uriTypeAdapter: Any): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Uri::class.java, uriTypeAdapter)
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    @Named("signup")
    fun provideSignupRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.SIGNUP)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("updateAccount")
    fun provideUpdateAccountRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.UPDATE_ACCOUNT)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("deleteAccount")
    fun provideDeleteAccountRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.DELETE_ACCOUNT)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("sendMessage")
    fun provideSendMessageRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.SEND_MESSAGE)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("login")
    fun provideLoginRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.LOGIN)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("getAccountInfo")
    fun provideGetAccountInfoRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.GET_ACCOUNT_INFO)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("checkEmail")
    fun provideCheckAccountExistsByEmailRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.CHECK_EMAIL)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("getAllFriends")
    fun provideGetAllFriendsRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.GET_ALL_FRIENDS)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("getChat")
    fun provideGetChatRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.GET_CHAT)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("getAllLastMessages")
    fun provideGetAllLastMessagesRetrofit(gson: Gson): Retrofit {
        val baseUrl = CloudFunctionUrlBuilder.buildBaseUrl(CloudFunctionNames.GET_ALL_LAST_MESSAGES)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}