package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInitialSetupStatusUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return settingsRepository.hasCompletedInitialSetup
    }
}