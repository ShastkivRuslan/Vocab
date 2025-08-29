package com.example.learnwordstrainer.utils

import com.example.learnwordstrainer.ui.addwordfloating.compose.state.UiError
import retrofit2.HttpException
import java.io.IOException
import com.example.learnwordstrainer.data.remote.client.WordInfoException

fun mapThrowableToUiError(throwable: Throwable?): UiError {
    return when (throwable) {
        is IOException -> UiError.NetworkError

        is WordInfoException.HttpApiError -> {
            when (throwable.code) {
                404 -> UiError.WordNotFound
                else -> UiError.UnknownError
            }
        }
        is WordInfoException.ParsingException,
        is WordInfoException.EmptyContentException -> {
            UiError.WordNotFound
        }

        is HttpException -> {
            when (throwable.code()) {
                404 -> UiError.WordNotFound
                else -> UiError.UnknownError
            }
        }

        else -> UiError.UnknownError
    }
}