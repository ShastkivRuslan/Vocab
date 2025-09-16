package com.shastkiv.vocab.ui.addword.internal

import AddWordScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddWordActivity : ComponentActivity() {

    private val viewModel: AddWordViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return addWordViewModelFactory.create() as T
            }
        }
    }

    @Inject
    lateinit var addWordViewModelFactory: AddWordViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val processedText = extractProcessedText()

        setContent {
            AddWordScreen(
                viewModel = viewModel,
                initialText = processedText,
                onFinish = { finish() }
            )
        }
    }

    private fun extractProcessedText(): String? {
        return when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            }
            Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)
            }
            else -> null
        }
    }
}