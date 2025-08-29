package com.example.learnwordstrainer.di

import com.example.learnwordstrainer.data.repository.*
import com.example.learnwordstrainer.domain.repository.BubbleSettingsRepository
import com.example.learnwordstrainer.domain.repository.DailyStatsRepository
import com.example.learnwordstrainer.domain.repository.LanguageRepository
import com.example.learnwordstrainer.domain.repository.SettingsRepository
import com.example.learnwordstrainer.domain.repository.ThemeRepository
import com.example.learnwordstrainer.domain.repository.WordDetailsCacheRepository
import com.example.learnwordstrainer.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWordRepository(
        wordRepositoryImpl: WordRepositoryImpl
    ): WordRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindWordDetailsCacheRepository(
        wordDetailsCacheRepositoryImpl: WordDetailsCacheRepositoryImpl
    ): WordDetailsCacheRepository

    @Binds
    @Singleton
    abstract fun bindBubbleSettingsRepository(
        bubbleSettingsRepositoryImpl: BubbleSettingsRepositoryImpl
    ): BubbleSettingsRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        statsRepositoryImpl: StatsRepositoryImpl
    ): DailyStatsRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        impl: LanguageRepositoryImpl
    ): LanguageRepository

}
