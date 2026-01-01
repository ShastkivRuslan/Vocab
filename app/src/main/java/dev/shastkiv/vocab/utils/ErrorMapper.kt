package dev.shastkiv.vocab.utils

import android.database.sqlite.SQLiteException
import dev.shastkiv.vocab.data.remote.client.WordInfoException
import dev.shastkiv.vocab.domain.model.UiError
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Конвертує будь-який Throwable в типізовану UiError для відображення в UI
 */
fun mapThrowableToUiError(throwable: Throwable?): UiError {
    return when (throwable) {
        // Network errors
        is UnknownHostException,
        is IOException -> UiError.NetworkError

        is SocketTimeoutException -> UiError.TimeoutError

        // Custom API errors
        is WordInfoException.HttpApiError -> {
            when (throwable.code) {
                404 -> UiError.WordNotFound
                in 500..599 -> UiError.UnknownError
                else -> UiError.UnknownError
            }
        }

        is WordInfoException.EmptyContentException,
        is WordInfoException.ParsingException -> UiError.ParsingError

        // Retrofit HTTP errors
        is HttpException -> {
            when (throwable.code()) {
                404 -> UiError.WordNotFound
                in 500..599 -> UiError.UnknownError
                else -> UiError.UnknownError
            }
        }

        // Database errors
        is SQLiteException -> UiError.DatabaseError

        // Empty data
        is NoSuchElementException -> UiError.EmptyData

        // Validation errors
        is IllegalArgumentException -> UiError.UnknownError

        // Unknown
        else -> UiError.UnknownError
    }
}