package pro.branium.messenger.data.repository

import pro.branium.messenger.domain.datasource.AccountDataSource
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.repository.AccountRepository
import pro.branium.messenger.presentation.screens.SignupState
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

    override suspend fun signup(account: Account): SignupState {
        return dataSource.createAccount(account)
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

    override suspend fun checkUsername(username: String): Boolean {
        return dataSource.checkUsername(username)
    }

    override suspend fun checkEmail(email: String): Boolean {
        return dataSource.checkEmail(email)
    }
}