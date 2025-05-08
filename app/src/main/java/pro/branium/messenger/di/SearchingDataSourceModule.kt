package pro.branium.messenger.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pro.branium.messenger.data.datasource.remote.SearchingDataSourceImpl
import pro.branium.messenger.domain.datasource.SearchingDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchingDataSourceModule {
    @Binds
    abstract fun bindSearchingDataSource(impl: SearchingDataSourceImpl): SearchingDataSource
}