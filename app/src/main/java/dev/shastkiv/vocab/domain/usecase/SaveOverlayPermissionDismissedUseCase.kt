package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveOverlayPermissionDismissedUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(dismissed: Boolean) {
        settingsRepository.setOverlayPermissionDismissed(dismissed)
    }
}