package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.repository.WordRepository
import javax.inject.Inject

data class RepetitionData(
    val word: Word,
    val options: List<String>
)

class GetRepetitionWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(): RepetitionData? {
        val randomWord = repository.getRandomWord() ?: return null

        val wrongOptions = repository.getRandomTranslations(randomWord.id)

        val finalOptions = mutableListOf<String>()
        finalOptions.addAll(wrongOptions)
        finalOptions.add(randomWord.translation)

        finalOptions.shuffle()

        return RepetitionData(
            word = randomWord,
            options = finalOptions
        )
    }
}