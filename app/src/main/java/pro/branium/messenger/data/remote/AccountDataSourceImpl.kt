package pro.branium.messenger.data.remote

import pro.branium.messenger.domain.datasource.AccountDataSource
import pro.branium.messenger.domain.model.Account
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class AccountDataSourceImpl @Inject constructor(
    @Named("createAccount") private val createAccountRetrofit: Retrofit,
    @Named("updateAccount") private val updateAccountRetrofit: Retrofit,
    @Named("deleteAccount") private val deleteAccountRetrofit: Retrofit,
    @Named("login") private val loginRetrofit: Retrofit,
    @Named("getAccountInfo") private val getAccountInfoRetrofit: Retrofit,
) : AccountDataSource {
    override suspend fun getAccount(username: String): Account? {
        val retrofit = getAccountInfoRetrofit.create(AccountService::class.java)
        val result = retrofit.getAccount(username)
        return result.body()
    }

    override suspend fun logout(account: Account): Boolean {
        // todo
        return true
    }

    override suspend fun createAccount(account: Account): String {
        val retrofit = createAccountRetrofit.create(AccountService::class.java)
        val result = retrofit.createAccount(account)
        return if (result.isSuccessful) {
            val responseObj = result.body()
            if (responseObj != null) {
                if (responseObj.success) {
                    "Success"
                } else {
                    responseObj.error!!
                }
            } else {
                "null"
            }
        } else {
            result.body()?.error!!
        }
    }

    override suspend fun updateAccount(account: Account): Boolean {
        val retrofit = updateAccountRetrofit.create(AccountService::class.java)
        val result = retrofit.updateAccount(account)
        return result.isSuccessful
    }

    override suspend fun deleteAccount(account: Account): Boolean {
        val retrofit = deleteAccountRetrofit.create(AccountService::class.java)
        val result = retrofit.deleteAccount(account.username, account.password)
        return result.isSuccessful
    }

    override suspend fun login(account: Account): Account? {
        val retrofit = loginRetrofit.create(AccountService::class.java)
        val result = retrofit.login(account)
        if (result.isSuccessful) {
            return result.body()
        }
        return null
    }

    override suspend fun forgotPassword(email: String): Boolean {
        // todo: implement this function
        return false
    }

    override suspend fun resetPassword(account: Account): Boolean {
        // todo: implement this function
        return false
    }
}