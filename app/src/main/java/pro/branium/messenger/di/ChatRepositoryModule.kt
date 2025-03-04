package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.repository.ChatRepositoryImpl
import pro.branium.messenger.domain.repository.ChatRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatRepositoryModule {
    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}