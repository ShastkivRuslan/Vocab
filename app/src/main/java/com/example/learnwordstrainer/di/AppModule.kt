package com.example.learnwordstrainer.di

import android.content.Context
import com.example.learnwordstrainer.domain.GetBubbleSettingsFlowUseCase
import com.example.learnwordstrainer.domain.SaveBubblePositionUseCase
import com.example.learnwordstrainer.repository.BubbleSettingsRepository
import com.example.learnwordstrainer.repository.ThemeRepository
import com.example.learnwordstrainer.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideWordRepository(@ApplicationContext context: Context): WordRepository {
        return WordRepository(context)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(@ApplicationContext context: Context): ThemeRepository {
        return ThemeRepository(context)
    }
}