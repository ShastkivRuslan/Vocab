package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.di.IoDispatcher // HОВИЙ ІМПОРТ
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineDispatcher // HОВИЙ ІМПОРТ
import kotlinx.coroutines.withContext // HОВИЙ ІМПОРТ
import javax.inject.Inject

data class RepetitionData(
    val word: Word,
    val options: List<String>
)

class GetRepetitionWordUseCase @Inject constructor(
    private val repository: WordRepository,
    private val getLanguageSettingsUseCase: GetLanguageSettingsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): RepetitionData? = withContext(ioDispatcher) {
        val settings = getLanguageSettingsUseCase()
        val sourceLanguageCode = settings.sourceLanguage.code
        val targetLanguageCode = settings.targetLanguage.code

        val randomWord = repository.getWordForRepetition(sourceLanguageCode)
            ?: return@withContext null

        val wrongOptions = repository.getAnswerOptionsForWord(
            wordToRepeat = randomWord,
            targetLanguageCode = targetLanguageCode
        )

        val finalOptions = mutableListOf<String>()
        finalOptions.addAll(wrongOptions)
        finalOptions.add(randomWord.translation)
        finalOptions.shuffle()

        return@withContext RepetitionData(
            word = randomWord,
            options = finalOptions
        )
    }
}