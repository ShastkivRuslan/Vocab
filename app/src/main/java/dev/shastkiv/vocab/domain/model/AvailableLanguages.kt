package dev.shastkiv.vocab.domain.model

object AvailableLanguages {
    val DEFAULT_UKRAINIAN = Language("uk", "Українська", "🇺🇦")
    val DEFAULT_ENGLISH = Language("en", "English", "🇬🇧")

    val list = listOf(
        DEFAULT_ENGLISH,
        Language("de", "Deutsch", "🇩🇪"),
        DEFAULT_UKRAINIAN,
        Language("fr", "Français", "🇫🇷"),
        Language("pl", "Polski", "🇵🇱"),
        Language("cs", "Čeština", "🇨🇿")
    )

    fun findByCode(code: String?): Language {
        return list.find { it.code.equals(code, ignoreCase = true) } ?: DEFAULT_ENGLISH
    }
}