package com.shastkiv.vocab.di

import android.content.Context
import androidx.room.Room
import com.shastkiv.vocab.data.local.dao.DailyStatisticDao
import com.shastkiv.vocab.data.local.dao.WordDao
import com.shastkiv.vocab.data.local.dao.WordDetailsCacheDao
import com.shastkiv.vocab.data.local.db.MIGRATION_7_8
import com.shastkiv.vocab.data.local.db.WordDatabase
import com.shastkiv.vocab.data.remote.api.OpenAIAPI
import com.google.gson.Gson
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
        )
            .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_7_8)
            .build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: WordDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    fun provideDailyStatisticDao(appDatabase: WordDatabase): DailyStatisticDao {
        return appDatabase.dailyStatisticDao()
    }


    @Provides
    @Singleton
    fun provideWordDetailsCacheDao(database: WordDatabase): WordDetailsCacheDao {
        return database.wordDetailsCacheDao()
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
    fun provideGson(): Gson = Gson()
}
