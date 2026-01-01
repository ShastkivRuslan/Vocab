package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveNotificationPermissionDismissedUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(dismissed: Boolean) {
        settingsRepository.setNotificationPermissionDismissed(dismissed)
    }
}