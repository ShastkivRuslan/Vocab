package dev.shastkiv.vocab.ui.widget.action

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dev.shastkiv.vocab.domain.model.WidgetClickAction
import dev.shastkiv.vocab.domain.repository.WidgetSettingsRepository
import dev.shastkiv.vocab.ui.widget.UpdateWidgetWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateWidgetAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UpdateWidgetActionEntryPoint {
        fun widgetSettingsRepository(): WidgetSettingsRepository
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            UpdateWidgetActionEntryPoint::class.java
        )
        val widgetSettingsRepository = entryPoint.widgetSettingsRepository()
        val settings = widgetSettingsRepository.getLatestWidgetSettings()

        Log.d("WidgetClick", "=== clickAction=${settings.clickAction} ===")
        when (settings.clickAction) {
            WidgetClickAction.NEXT_WORD -> updateWidget(context, glanceId)
            WidgetClickAction.OPEN_APP -> openApp(context)
        }
    }

    private suspend fun updateWidget(context: Context, glanceId: GlanceId) {
        withContext(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            val request = OneTimeWorkRequestBuilder<UpdateWidgetWorker>().build()

            workManager.enqueueUniqueWork(
                "widget_click_update_${glanceId}",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    private fun openApp(context: Context) {
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            launchIntent?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        } catch (e: Exception) {
            android.util.Log.e("UpdateWidgetAction", "Error opening app", e)
        }
    }


}