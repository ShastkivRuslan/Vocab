package dev.shastkiv.vocab.domain.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import dev.shastkiv.vocab.R

sealed class UiError(
    @RawRes val animationRes: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
) {
    // Network errors
    object NetworkError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_network_title,
        description = R.string.error_network_description
    )

    object TimeoutError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_timeout_title,
        description = R.string.error_timeout_description
    )

    // Data errors
    object WordNotFound : UiError(
        animationRes = R.raw.error,
        title = R.string.error_not_found_title,
        description = R.string.error_not_found_description
    )

    object DatabaseError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_database_title,
        description = R.string.error_database_description
    )

    object EmptyData : UiError(
        animationRes = R.raw.error,
        title = R.string.error_empty_title,
        description = R.string.error_empty_description
    )

    object ParsingError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_parsing_title,
        description = R.string.error_parsing_description
    )

    object UnknownError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_unknown_title,
        description = R.string.error_unknown_description
    )

    object InvalidWord : UiError(
        animationRes = R.raw.error,
        title = R.string.error_invalid_word_title,
        description = R.string.error_invalid_word_description
    )

    object WrongLanguage : UiError(
        animationRes = R.raw.wrong_language,
        title = R.string.error_wrong_lang_title,
        description = R.string.error_wrong_lang_description
    )
}