package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class AddWordToDictionaryUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(
        sourceWord: String,
        translation: String,
        sourceLanguageCode: String,
        targetLanguageCode: String,
        wordLevel: String
    ) {
        if (sourceWord.isNotBlank() && translation.isNotBlank()) {
            val newWord = Word(
                sourceWord = sourceWord,
                translation = translation,
                sourceLanguageCode = sourceLanguageCode,
                targetLanguageCode = targetLanguageCode,
                wordLevel = wordLevel
            )
            repository.addWord(newWord)
        }
    }
}