package dev.shastkiv.vocab.domain.exeptions

sealed class LinguisticException() : Exception() {
    class InvalidWord() : LinguisticException()
    class WrongLanguage() : LinguisticException()
}
