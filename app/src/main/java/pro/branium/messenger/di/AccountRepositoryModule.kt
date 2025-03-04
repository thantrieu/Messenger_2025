package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.repository.AccountRepositoryImpl
import pro.branium.messenger.domain.repository.AccountRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountRepositoryModule {
    @Binds
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}