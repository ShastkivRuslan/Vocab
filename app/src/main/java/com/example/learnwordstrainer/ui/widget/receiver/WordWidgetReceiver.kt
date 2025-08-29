package com.example.learnwordstrainer.ui.widget.receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.learnwordstrainer.ui.widget.UpdateWidgetWorker
import com.example.learnwordstrainer.ui.widget.WordWidget

class WordWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WordWidget

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val workManager = WorkManager.getInstance(context)
        val initialRequest = OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
            .addTag("initial_widget_update")
            .build()
        workManager.enqueue(initialRequest)
    }
}