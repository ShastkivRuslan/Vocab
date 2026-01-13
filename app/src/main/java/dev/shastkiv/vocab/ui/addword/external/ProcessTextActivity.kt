package dev.shastkiv.vocab.ui.addword.external

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.shastkiv.vocab.ui.addword.external.compose.ProcessTextScreen
import dev.shastkiv.vocab.ui.addword.shared.AddWordViewModelProvider
import dev.shastkiv.vocab.ui.theme.VocabAppCoreTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme

@AndroidEntryPoint
class ProcessTextActivity : ComponentActivity() {

    private val viewModelProvider: AddWordViewModelProvider by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTransparentWindow()

        val processedText = extractProcessedText()

        setContent {
            val currentTheme by viewModelProvider.addWordViewModel.themeMode.collectAsState()

            LearnWordsTrainerTheme(
                themeMode = currentTheme
            ) {
                ProcessTextScreen(
                    viewModel = viewModelProvider.addWordViewModel,
                    initialText = processedText,
                    onFinish = { finish() }
                )
            }
        }
    }

    private fun setupTransparentWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            )
            window.attributes.blurBehindRadius = 25
        }

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
    }

    private fun extractProcessedText(): String? {
        return when {
            intent?.action == Intent.ACTION_PROCESS_TEXT -> {
                intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            }
            intent?.action == Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            }
            else -> null
        }
    }

    companion object {
        fun createNavigationIntent(context: Context): Intent {
            return Intent(context, ProcessTextActivity::class.java)
        }
    }
}