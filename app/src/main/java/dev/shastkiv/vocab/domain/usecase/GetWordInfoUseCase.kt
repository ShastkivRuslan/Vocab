package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.data.remote.client.OpenAIClient
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.domain.model.enums.WordType
import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class GetWordInfoUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val openAIClient: OpenAIClient
) {
    suspend operator fun invoke(
        word: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): Result<WordData> {
        if (word.isBlank()) {
            return Result.failure(IllegalArgumentException("Слово не може бути порожнім"))
        }

        val cachedWord = wordRepository.getCachedWord(word, sourceLanguage.code)
        if (cachedWord?.hasAIData() == true) {
            return Result.success(cachedWord.getAIData()!!)
        }

        val apiResult = openAIClient.fetchWordInfo(word, sourceLanguage.name, targetLanguage.name)

        apiResult.onSuccess { wordData ->
            val cacheWord = Word(
                sourceWord = word,
                translation = wordData.translation,
                sourceLanguageCode = sourceLanguage.code,
                targetLanguageCode = targetLanguage.code,
                wordType = WordType.CACHE_ONLY,
                isWordAdded = false,
                aiDataJson = wordData.toJson()
            )
            wordRepository.addWord(cacheWord)
        }

        return apiResult
    }
}