package dev.shastkiv.vocab.ui.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.AvailableLanguages
import dev.shastkiv.vocab.domain.usecase.GetWidgetWordUseCase
import dev.shastkiv.vocab.domain.usecase.ShouldShowTranslationUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UpdateWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getWidgetWordUseCase: GetWidgetWordUseCase,
    private val shouldShowTranslationUseCase: ShouldShowTranslationUseCase
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(WordWidget::class.java)

            if (glanceIds.isEmpty()) {
                return@withContext Result.success()
            }

            // Отримуємо слово з урахуванням налаштувань фільтрації
            val randomWord = getWidgetWordUseCase()

            // Перевіряємо чи показувати переклад
            val shouldShowTranslation = shouldShowTranslationUseCase()

            val sourceLanguage = AvailableLanguages.findByCode(randomWord?.sourceLanguageCode)

            glanceIds.forEach { glanceId ->
                try {
                    updateAppWidgetState(context, glanceId) { prefs ->
                        val sourceText = randomWord?.sourceWord ?: context.getString(R.string.vocab_empty)

                        // Показуємо переклад тільки якщо ввімкнено в налаштуваннях
                        val translationText = if (shouldShowTranslation) {
                            randomWord?.translation ?: context.getString(R.string.add_new_words_widget)
                        } else {
                            "***" // Приховуємо переклад
                        }

                        val wordLevel = randomWord?.level ?: ""
                        val flagEmoji = sourceLanguage.flagEmoji

                        prefs[WidgetStateKeys.sourceWordKey] = sourceText
                        prefs[WidgetStateKeys.translationKey] = translationText
                        prefs[WidgetStateKeys.sourceFlagEmojiKey] = flagEmoji
                        prefs[WidgetStateKeys.wordLevel] = wordLevel
                        prefs[WidgetStateKeys.updatedAt] = System.currentTimeMillis()
                    }

                    WordWidget.update(context, glanceId)
                } catch (e: Exception) {
                    Log.e("UpdateWidgetWorker", "Error updating widget $glanceId", e)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("UpdateWidgetWorker", "General error in doWork", e)
            Result.failure()
        }
    }
}