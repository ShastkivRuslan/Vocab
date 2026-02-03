package dev.shastkiv.vocab.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dev.shastkiv.vocab.data.local.dao.DailyStatisticDao
import dev.shastkiv.vocab.data.local.dao.WordDao
import dev.shastkiv.vocab.data.local.db.WordDatabase
import dev.shastkiv.vocab.data.remote.api.OpenAIAPI
import com.google.gson.Gson
import dev.shastkiv.vocab.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shastkiv.vocab.data.local.db.MIGRATION_1_2
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "global_settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.openai.com/"

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf(
            "openai_api_key" to ""
        )
        remoteConfig.setDefaultsAsync(defaults)

        remoteConfig.fetchAndActivate()

        return remoteConfig
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        return Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            "words.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
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