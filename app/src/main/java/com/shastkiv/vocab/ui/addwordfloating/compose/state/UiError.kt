package com.shastkiv.vocab.ui.addwordfloating.compose.state

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.shastkiv.vocab.R

sealed class UiError(
    @RawRes val animationRes: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
) {
    object NetworkError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_network_title,
        description = R.string.error_network_description
    )

    object WordNotFound : UiError(
        animationRes = R.raw.error,
        title = R.string.error_not_found_title,
        description = R.string.error_not_found_description
    )

    object UnknownError : UiError(
        animationRes = R.raw.error,
        title = R.string.error_unknown_title,
        description = R.string.error_unknown_description
    )
}
