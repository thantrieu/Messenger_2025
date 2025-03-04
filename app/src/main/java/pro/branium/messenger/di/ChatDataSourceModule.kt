package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.remote.ChatDataSourceImpl
import pro.branium.messenger.domain.datasource.ChatDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataSourceModule {
    @Binds
    abstract fun bindChatDataSource(impl: ChatDataSourceImpl): ChatDataSource
}