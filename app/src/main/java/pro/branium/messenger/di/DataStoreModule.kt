package pro.branium.messenger.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// extension method for DataStore
private val Context.dataStore by preferencesDataStore(name="user_preference")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun providePreferenceDataStore(@ApplicationContext context: Context) = context.dataStore
}