package dev.shastkiv.vocab.data.remote.client

sealed class WordInfoException(message: String) : Exception(message) {
    class HttpApiError(val code: Int, message: String) : WordInfoException("API Error: $code, $message")

    object EmptyContentException : WordInfoException("Отримано порожню відповідь від сервера")

    object ParsingException : WordInfoException("Не вдалося розпарсити відповідь від AI")
}