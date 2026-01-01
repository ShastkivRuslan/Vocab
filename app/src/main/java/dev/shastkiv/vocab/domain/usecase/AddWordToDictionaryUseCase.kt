package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.domain.model.enums.WordType
import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class AddWordToDictionaryUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(
        sourceWord: String,
        translation: String,
        sourceLanguageCode: String,
        targetLanguageCode: String,
        wordType: WordType = WordType.FREE,
        wordData: WordData? = null
    ) {
        if (sourceWord.isNotBlank() && translation.isNotBlank()) {

            val cachedWord = repository.getCachedWord(sourceWord, sourceLanguageCode)

            if (cachedWord != null && cachedWord.hasAIData() && wordType != WordType.FREE) {
                repository.updateToUserDictionary(cachedWord.id, wordType)
            } else {
                val newWord = Word(
                    sourceWord = sourceWord,
                    translation = translation,
                    sourceLanguageCode = sourceLanguageCode,
                    targetLanguageCode = targetLanguageCode,
                    wordType = wordType,
                    isWordAdded = true,
                    aiDataJson = wordData?.toJson()
                )
                repository.addWord(newWord)
            }
        }
    }
}