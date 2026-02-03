package dev.shastkiv.vocab.domain.model.enums

import androidx.annotation.StringRes
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.CategoryCounts

enum class WordCategory(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
) {
    INTELLIGENT(
        R.string.category_intelligent,
        R.string.category_intelligent_desc
    ),

    NEW(
        R.string.category_new,
        R.string.category_new_desc
    ),

    HARD(
        R.string.category_hard,
        R.string.category_hard_desc
    ),

    STABLE(
        R.string.category_stable,
        R.string.category_stable_desc
    ),

    LEARNED(
        R.string.category_learned,
        R.string.category_learned_desc
    );

    fun getCount(counts: CategoryCounts): Int {
        return when (this) {
            NEW -> counts.newCount
            HARD -> counts.hardCount
            STABLE -> counts.stableCount
            LEARNED -> counts.learnedCount
            INTELLIGENT -> counts.totalCount
        }
    }
}