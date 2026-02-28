package dev.shastkiv.vocab.ui.widget.glance

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.widget.WidgetStateKeys
import dev.shastkiv.vocab.ui.widget.action.UpdateWidgetAction
import dev.shastkiv.vocab.utils.formatTimestamp

@Composable
fun WidgetUi() {
    val prefs: Preferences = currentState()
    val sourceWord = prefs[WidgetStateKeys.sourceWordKey] ?: "Vocab."
    val translation = prefs[WidgetStateKeys.translationKey] ?: "..."
    val flagEmoji = prefs[WidgetStateKeys.sourceFlagEmojiKey] ?: "🇬🇧"
    val wordLevel = prefs[WidgetStateKeys.wordLevel] ?: ""
    val lastUpdateTimeMillis = prefs[WidgetStateKeys.updatedAt]
    val context = LocalContext.current

    val size = LocalSize.current
    val sourceFontSize = if (size.height > 80.dp) 24.sp else 18.sp
    val translationFontSize = if (size.height > 80.dp) 18.sp else 14.sp

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .clickable(onClick = actionRunCallback<UpdateWidgetAction>())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(R.string.app_name),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
            Spacer(GlanceModifier.width(4.dp))
            Text(
                text = flagEmoji,
                style = TextStyle(fontSize = 12.sp)
            )
            Spacer(GlanceModifier.defaultWeight())
            Text(
                text = "${formatTimestamp(lastUpdateTimeMillis)} \uD83D\uDD52",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant)
            )
        }

        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(
                    text = sourceWord,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = sourceFontSize,
                        color = GlanceTheme.colors.onBackground
                    )
                )
                if (wordLevel.isNotBlank()) {
                    Spacer(GlanceModifier.width(4.dp))
                    Text(
                        text = wordLevel,
                        style = TextStyle(
                            fontSize = translationFontSize,
                            color = GlanceTheme.colors.primary
                        ),
                        modifier = GlanceModifier
                            .background(GlanceTheme.colors.primaryContainer)
                            .cornerRadius(4.dp)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = translation,
                style = TextStyle(
                    fontSize = translationFontSize,
                    color = GlanceTheme.colors.onBackground,
                )
            )
        }
    }
}

@Composable
private fun GlanceModifier.appWidgetBackground(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12+ (API 31+): Використовуємо системний радіус
        this.background(GlanceTheme.colors.surface)
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        // Android 10-11 (API 29-30): Використовуємо фіксований радіус
        this.background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
    }
}