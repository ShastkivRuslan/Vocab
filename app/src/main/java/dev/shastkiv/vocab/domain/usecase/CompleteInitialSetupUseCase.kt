package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.repository.SettingsRepository
import javax.inject.Inject

class CompleteInitialSetupUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        settingsRepository.setInitialSetupCompleted(true)
    }
}