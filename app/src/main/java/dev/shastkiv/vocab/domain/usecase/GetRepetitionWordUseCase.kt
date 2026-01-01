package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.RepetitionData
import dev.shastkiv.vocab.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

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