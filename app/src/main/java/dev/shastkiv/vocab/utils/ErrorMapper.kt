package dev.shastkiv.vocab.utils

import android.database.sqlite.SQLiteException
import dev.shastkiv.vocab.data.remote.client.WordInfoException
import dev.shastkiv.vocab.domain.exeptions.LinguisticException
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
        is LinguisticException.InvalidWord -> UiError.InvalidWord
        is LinguisticException.WrongLanguage -> UiError.WrongLanguage

        is UnknownHostException,
        is IOException -> UiError.NetworkError

        is SocketTimeoutException -> UiError.TimeoutError

        is WordInfoException.HttpApiError -> {
            when (throwable.code) {
                404 -> UiError.WordNotFound
                in 500..599 -> UiError.UnknownError
                else -> UiError.UnknownError
            }
        }

        is WordInfoException.EmptyContentException,
        is WordInfoException.ParsingException -> UiError.ParsingError

        is HttpException -> {
            when (throwable.code()) {
                404 -> UiError.WordNotFound
                in 500..599 -> UiError.UnknownError
                else -> UiError.UnknownError
            }
        }

        is SQLiteException -> UiError.DatabaseError

        is NoSuchElementException -> UiError.EmptyData

        is IllegalArgumentException -> UiError.UnknownError

        // Unknown
        else -> UiError.UnknownError
    }
}