package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.datasource.remote.AuthRemoteDataSourceImpl
import pro.branium.messenger.domain.datasource.AuthRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountDataSourceModule {
    @Binds
    abstract fun bindAccountDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource
}