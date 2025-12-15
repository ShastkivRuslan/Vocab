package com.shastkiv.vocab.di

import com.shastkiv.vocab.data.repository.*
import com.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import com.shastkiv.vocab.domain.repository.StatisticRepository
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.shastkiv.vocab.domain.repository.OnDeviceTranslator
import com.shastkiv.vocab.domain.repository.SettingsRepository
import com.shastkiv.vocab.domain.repository.ThemeRepository
import com.shastkiv.vocab.domain.repository.WordRepository
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
    abstract fun bindOnDeviceTranslator(
        onDeviceTranslatorRepository: OnDeviceTranslatorRepository
    ): OnDeviceTranslator


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
    abstract fun bindStatisticRepository(
        statisticRepositoryImpl: StatisticRepositoryImpl
    ): StatisticRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        impl: LanguageRepositoryImpl
    ): LanguageRepository

}
