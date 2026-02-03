package dev.shastkiv.vocab.domain.model.enums

enum class ExerciseType(
    val weight: Int) {
    QUIZ(10),
    CARDS(5),
    SPRINT(8),
    CONSTRUCTOR(15),
    WRITING(25);

    companion object {
        const val MASTERY_PENALTY_COEFFICIENT = 1.5
    }

    fun calculateNewScore(currentScore: Int, isCorrect: Boolean): Int {
        val change = if (isCorrect) {
            weight
        } else {
            -(weight * MASTERY_PENALTY_COEFFICIENT).toInt()
        }
        return (currentScore + change).coerceIn(0, 100)
    }
}