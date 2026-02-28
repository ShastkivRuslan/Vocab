package dev.shastkiv.vocab.domain.usecase

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shastkiv.vocab.domain.repository.WidgetSettingsRepository
import dev.shastkiv.vocab.ui.widget.UpdateWidgetWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Use Case для планування періодичних оновлень віджета
 * відповідно до налаштувань користувача
 */
class ScheduleWidgetUpdatesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val widgetSettingsRepository: WidgetSettingsRepository
) {
    suspend operator fun invoke() {
        val settings = widgetSettingsRepository.getLatestWidgetSettings()
        val workManager = WorkManager.getInstance(context)

        when {
            // Якщо оновлення вимкнено (0 хвилин)
            settings.updateFrequencyMinutes == 0 -> {
                workManager.cancelUniqueWork(WIDGET_WORK_NAME)
            }

            // Якщо менше 15 хвилин, використовуємо мінімум (15 хв - обмеження Android)
            settings.updateFrequencyMinutes < 15 -> {
                scheduleWork(workManager, 15)
            }

            // Інакше використовуємо налаштований інтервал
            else -> {
                scheduleWork(workManager, settings.updateFrequencyMinutes)
            }
        }
    }

    private fun scheduleWork(workManager: WorkManager, intervalMinutes: Int) {
        val periodicRequest = PeriodicWorkRequestBuilder<UpdateWidgetWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        ).build()

        // REPLACE означає: оновлюємо існуючу задачу з новим інтервалом
        workManager.enqueueUniquePeriodicWork(
            WIDGET_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }

    companion object {
        private const val WIDGET_WORK_NAME = "periodic_widget_update_work"
    }
}