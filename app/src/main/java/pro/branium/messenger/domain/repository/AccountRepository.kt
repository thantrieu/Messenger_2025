package pro.branium.messenger.domain.repository

import pro.branium.messenger.domain.model.Account

interface AccountRepository {
    suspend fun login(account: Account): Account?
    suspend fun logout(account: Account): Boolean
    suspend fun signup(account: Account): Boolean
    suspend fun updateAccount(account: Account): Boolean
    suspend fun deleteAccount(account: Account): Boolean
    suspend fun forgotPassword(email: String): Boolean
    suspend fun resetPassword(account: Account): Boolean
    suspend fun getAccountByUsername(username: String): Account?
}