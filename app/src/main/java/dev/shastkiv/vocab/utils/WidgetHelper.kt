package dev.shastkiv.vocab.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import dev.shastkiv.vocab.ui.widget.receiver.WordWidgetReceiver
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Long?): String {
    if (timestamp == null) return ""
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}

object WidgetManager {

    suspend fun isWidgetInstalled(context: Context): Boolean {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(dev.shastkiv.vocab.ui.widget.WordWidget::class.java)
        return glanceIds.isNotEmpty()
    }

    fun requestPinWidget(context: Context) {
        val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(context, WordWidgetReceiver::class.java)

        if (appWidgetManager?.isRequestPinAppWidgetSupported == true) {
            appWidgetManager.requestPinAppWidget(myProvider, null, null)
        }
    }
}
