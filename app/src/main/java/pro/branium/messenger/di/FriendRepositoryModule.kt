package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.repository.FriendRepositoryImpl
import pro.branium.messenger.domain.repository.FriendRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class FriendRepositoryModule {
    @Binds
    abstract fun bindFriendRepository(impl: FriendRepositoryImpl): FriendRepository
}