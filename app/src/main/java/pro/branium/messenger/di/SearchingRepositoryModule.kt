package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.repository.SearchingRepositoryImpl
import pro.branium.messenger.domain.repository.SearchingRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchingRepositoryModule {
    @Binds
    abstract fun bindSearchingRepository(
        searchingRepositoryImpl: SearchingRepositoryImpl
    ): SearchingRepository
}