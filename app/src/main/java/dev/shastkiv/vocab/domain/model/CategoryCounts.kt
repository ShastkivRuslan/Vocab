package dev.shastkiv.vocab.domain.model

data class CategoryCounts(
    val totalCount: Int = 0,
    val newCount: Int = 0,
    val hardCount: Int = 0,
    val stableCount: Int = 0,
    val learnedCount: Int = 0
)