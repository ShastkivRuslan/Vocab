package com.example.learnwordstrainer.di

import android.content.Context
import androidx.room.Room
import com.example.learnwordstrainer.data.local.dao.WordDao
import com.example.learnwordstrainer.data.local.dao.WordDetailsCacheDao
import com.example.learnwordstrainer.data.local.db.WordDatabase
import com.example.learnwordstrainer.data.remote.api.OpenAIAPI
import com.example.learnwordstrainer.data.remote.client.OpenAIClient
import com.example.learnwordstrainer.domain.usecase.GetBubbleSettingsFlowUseCase
import com.example.learnwordstrainer.domain.usecase.SaveBubblePositionUseCase
import com.example.learnwordstrainer.data.repository.BubbleSettingsRepository
import com.example.learnwordstrainer.data.repository.ThemeRepository
import com.example.learnwordstrainer.data.repository.WordDetailsCacheRepository
import com.example.learnwordstrainer.data.repository.WordDetailsCacheRepositoryImpl
import com.example.learnwordstrainer.data.repository.WordRepository
import com.example.learnwordstrainer.data.repository.WordRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.openai.com/"

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        return Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            "words.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: WordDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepositoryImpl(wordDao)
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAiApi(retrofit: Retrofit): OpenAIAPI {
        return retrofit.create(OpenAIAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideBubbleSettingsRepository(
        @ApplicationContext context: Context
    ): BubbleSettingsRepository {
        return BubbleSettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideGetBubbleSettingsFlowUseCase(
        repository: BubbleSettingsRepository
    ): GetBubbleSettingsFlowUseCase {
        return GetBubbleSettingsFlowUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveBubblePositionUseCase(
        repository: BubbleSettingsRepository
    ): SaveBubblePositionUseCase {
        return SaveBubblePositionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(@ApplicationContext context: Context): ThemeRepository {
        return ThemeRepository(
            context
        )
    }

    @Provides
    @Singleton
    fun provideWordDetailsCacheDao(database: WordDatabase): WordDetailsCacheDao {
        return database.wordDetailsCacheDao()
    }

    @Provides
    @Singleton
    fun provideWordDetailsCacheRepository(
        cacheDao: WordDetailsCacheDao,
        openAIClient: OpenAIClient
    ): WordDetailsCacheRepository {
        return WordDetailsCacheRepositoryImpl(cacheDao, openAIClient)
    }
}