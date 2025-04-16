package pro.branium.messenger.domain.datasource

import pro.branium.messenger.domain.model.Account

interface AccountDataSource {
    suspend fun getAccount(username: String): Account?
    suspend fun logout(account: Account): Boolean
    suspend fun createAccount(account: Account): String
    suspend fun updateAccount(account: Account): Boolean
    suspend fun deleteAccount(account: Account): Boolean
    suspend fun login(account: Account): Account?
    suspend fun forgotPassword(email: String): Boolean
    suspend fun resetPassword(account: Account): Boolean
    suspend fun checkUsername(username: String): Boolean
    suspend fun checkEmail(email: String): Boolean
}