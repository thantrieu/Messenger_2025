package pro.branium.messenger.data.repository

import pro.branium.messenger.domain.datasource.AccountDataSource
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val dataSource: AccountDataSource
) : AccountRepository {
    override suspend fun login(account: Account): Account? {
        return dataSource.login(account)
    }

    override suspend fun logout(account: Account): Boolean {
        return dataSource.logout(account)
    }

    override suspend fun signup(account: Account): Boolean {
        return dataSource.createAccount(account) == "Success"
    }

    override suspend fun updateAccount(account: Account): Boolean {
        return dataSource.updateAccount(account)
    }

    override suspend fun deleteAccount(account: Account): Boolean {
        return dataSource.deleteAccount(account)
    }

    override suspend fun forgotPassword(email: String): Boolean {
        return dataSource.forgotPassword(email)
    }

    override suspend fun resetPassword(account: Account): Boolean {
        return dataSource.resetPassword(account)
    }

    override suspend fun getAccountByUsername(username: String): Account? {
        return dataSource.getAccount(username)
    }
}