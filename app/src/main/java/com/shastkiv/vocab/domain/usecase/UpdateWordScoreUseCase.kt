package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class UpdateWordScoreUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(
        wordId: Int,
        currentCorrectCount: Int,
        currentWrongCount: Int,
        wasAnswerCorrect: Boolean
    ) {
        val newCorrectCount = if (wasAnswerCorrect) currentCorrectCount + 1 else currentCorrectCount
        val newWrongCount = if (!wasAnswerCorrect) currentWrongCount + 1 else currentWrongCount

        repository.updateScore(
            id = wordId,
            correctCount = newCorrectCount,
            wrongCount = newWrongCount
        )
    }
}