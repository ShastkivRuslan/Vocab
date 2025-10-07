package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.WordType
import com.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class GetPracticeWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(): List<Word> {
        return listOf(
            Word(
                id = 1,
                sourceWord = "Apple",
                translation = "Яблуко",
                sourceLanguageCode = "en",
                targetLanguageCode = "uk",
                wordType = WordType.FREE,
                isWordAdded = true
            ),
            Word(
                id = 2,
                sourceWord = "Banana",
                translation = "Банан",
                sourceLanguageCode = "en",
                targetLanguageCode = "uk",
                wordType = WordType.FREE,
                isWordAdded = true
            )
        )
    }
}