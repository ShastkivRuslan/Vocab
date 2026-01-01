package dev.shastkiv.vocab.ui.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.shastkiv.vocab.ui.widget.UpdateWidgetWorker

class UpdateWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<UpdateWidgetWorker>().build()

        workManager.enqueueUniqueWork(
            "widget_click_update_${glanceId}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}