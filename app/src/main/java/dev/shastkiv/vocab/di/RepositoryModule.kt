package dev.shastkiv.vocab.di

import dev.shastkiv.vocab.data.repository.*
import dev.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import dev.shastkiv.vocab.domain.repository.StatisticRepository
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import dev.shastkiv.vocab.domain.repository.OnDeviceTranslator
import dev.shastkiv.vocab.domain.repository.SettingsRepository
import dev.shastkiv.vocab.domain.repository.ThemeRepository
import dev.shastkiv.vocab.domain.repository.WordRepository
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
