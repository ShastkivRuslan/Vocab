package dev.shastkiv.vocab.ui.widget.receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.shastkiv.vocab.domain.usecase.ScheduleWidgetUpdatesUseCase
import dev.shastkiv.vocab.ui.widget.UpdateWidgetWorker
import dev.shastkiv.vocab.ui.widget.WordWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WordWidgetReceiver : GlanceAppWidgetReceiver() {

    @Inject
    lateinit var scheduleWidgetUpdatesUseCase: ScheduleWidgetUpdatesUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override val glanceAppWidget: GlanceAppWidget = WordWidget

    // Цей метод викликається, коли ДОДАЄТЬСЯ перший віджет на екран
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleUpdates()
    }

    // Цей метод викликається, коли ВИДАЛЯЄТЬСЯ останній віджет
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork(WIDGET_WORK_NAME)
    }

    // Стандартний метод оновлення
    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Миттєве оновлення віджета
        triggerImmediateUpdate(context)

        // Перепланування періодичних оновлень
        scheduleUpdates()
    }

    /**
     * Планує періодичні оновлення через ScheduleWidgetUpdatesUseCase
     */
    private fun scheduleUpdates() {
        scope.launch {
            try {
                scheduleWidgetUpdatesUseCase()
            } catch (e: Exception) {
                // Якщо щось пішло не так, використовуємо дефолтний інтервал
                android.util.Log.e("WordWidgetReceiver", "Error scheduling updates", e)
            }
        }
    }

    /**
     * Запускає миттєве оновлення віджета
     */
    private fun triggerImmediateUpdate(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<UpdateWidgetWorker>().build()

        workManager.enqueueUniqueWork(
            "widget_immediate_update",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    companion object {
        private const val WIDGET_WORK_NAME = "periodic_widget_update_work"
    }
}