package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import javax.inject.Inject


class SaveBubblePositionUseCase @Inject constructor(
    private val repository: BubbleSettingsRepository
) {
    suspend operator fun invoke(x: Int, y: Int) = repository.savePosition(x, y)
}