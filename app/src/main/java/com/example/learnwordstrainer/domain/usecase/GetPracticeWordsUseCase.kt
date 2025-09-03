package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.repository.WordRepository
import javax.inject.Inject

class GetPracticeWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository

){
    suspend operator fun invoke(): List<Word> {

        return listOf(
            Word(1, "Apple", "Яблуко", "en", "uk", "A1"),
            Word(2, "Banana", "Банан", "en", "uk", "A1"),
        )
    }
}