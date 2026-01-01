package dev.shastkiv.vocab.ui.widget

import android.content.Context

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import dev.shastkiv.vocab.ui.theme.WidgetTheme
import dev.shastkiv.vocab.ui.widget.glance.WidgetUi

object WordWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(180.dp, 40.dp),
            DpSize(180.dp, 110.dp),
            DpSize(250.dp, 110.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetTheme {
                WidgetUi()
            }
        }
    }
}