package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.enums.ExerciseType
import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class UpdateWordScoreUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(
        wordId: Int,
        currentCorrectCount: Int,
        currentWrongCount: Int,
        currentMastery: Int,
        exerciseType: ExerciseType,
        wasAnswerCorrect: Boolean
    ) {
        val newCorrectCount = if (wasAnswerCorrect) currentCorrectCount + 1 else currentCorrectCount
        val newWrongCount = if (!wasAnswerCorrect) currentWrongCount + 1 else currentWrongCount

        val newMastery = exerciseType.calculateNewScore(
            currentScore = currentMastery,
            isCorrect = wasAnswerCorrect
        )


        repository.updateScore(
            id = wordId,
            correctCount = newCorrectCount,
            wrongCount = newWrongCount,
            masteryScore = newMastery,
            lastTrainedAt = System.currentTimeMillis()
        )
    }
}