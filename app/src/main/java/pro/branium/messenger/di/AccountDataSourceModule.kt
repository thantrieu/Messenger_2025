package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.remote.AccountDataSourceImpl
import pro.branium.messenger.domain.datasource.AccountDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountDataSourceModule {
    @Binds
    abstract fun bindAccountDataSource(impl: AccountDataSourceImpl): AccountDataSource
}