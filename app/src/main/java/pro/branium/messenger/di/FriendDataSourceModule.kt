package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.remote.FriendDataSourceImpl
import pro.branium.messenger.domain.datasource.FriendDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class FriendDataSourceModule {
    @Binds
    abstract fun bindFriendDataSource(impl: FriendDataSourceImpl): FriendDataSource
}