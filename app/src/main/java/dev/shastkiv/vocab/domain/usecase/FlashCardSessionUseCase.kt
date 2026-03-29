package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.enums.WordCategory
import dev.shastkiv.vocab.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlashCardSessionUseCase @Inject constructor(
    private val repository: WordRepository,
    private val getLanguageSettingsUseCase: GetLanguageSettingsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        limit: Int = 10,
        category: WordCategory
    ): List<Word> = withContext(ioDispatcher) {
        val settings = getLanguageSettingsUseCase()
        val sourceLanguageCode = settings.sourceLanguage.code

        when (category) {
            WordCategory.NEW -> repository.getNewWords(
                lang = sourceLanguageCode,
                limit = limit
            )
            WordCategory.HARD -> repository.getHardWords(
                lang = sourceLanguageCode,
                limit = limit
            )
            WordCategory.STABLE -> repository.getStableWords(
                lang = sourceLanguageCode,
                limit = limit
            )
            WordCategory.LEARNED -> repository.getLearnedWords(
                lang = sourceLanguageCode,
                limit = limit
            )
            WordCategory.INTELLIGENT -> repository.getIntelligentWords(
                lang = sourceLanguageCode,
                currentTime = System.currentTimeMillis(),
                limit = limit
            )
        }
    }
}