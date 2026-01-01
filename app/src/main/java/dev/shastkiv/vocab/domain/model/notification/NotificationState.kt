package dev.shastkiv.vocab.domain.model.notification

import dev.shastkiv.vocab.domain.model.Word

sealed class NotificationState {
    data class GentleNudge(val wordCount: Int) : NotificationState()
    data class WordEcho(val word: Word, val daysAgo: Int) : NotificationState()
    data class StreakKeeper(val streakDays: Int) : NotificationState()
    data class SuccessMoment(val word: String) : NotificationState()
}