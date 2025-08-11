package com.example.learnwordstrainer.ui.addwordfloating

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.learnwordstrainer.ui.addwordfloating.compose.AddWordDialog
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWordFloatingActivity : ComponentActivity() {

    private val viewModel: AddWordFloatingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedText = extractSelectedText()
        viewModel.initialize(selectedText)

        setContent {
            LearnWordsTrainerTheme {
                val uiState by viewModel.uiState.collectAsState()
                val inputWord by viewModel.inputWord.collectAsState()
                LaunchedEffect(uiState) {
                    when (uiState) {
                        is AddWordUiState.Error -> {
                            viewModel.onErrorShown()
                        }
                        is AddWordUiState.DialogShouldClose -> {
                            finish()
                        }
                        else -> {}
                    }
                }

                AddWordDialog(
                    uiState = uiState,
                    inputWord = inputWord,
                    onInputChange = viewModel::onInputChange,
                    onCheckWord = viewModel::onCheckWord,
                    onAddToVocabulary = viewModel::onAddWord,
                    onTextToSpeech = viewModel::onTextToSpeech,
                    onMainInfoToggle = viewModel::onMainInfoToggle,
                    onExamplesToggle = viewModel::onExamplesToggle,
                    ontContextToggle = viewModel::onContextToggle,
                    onDismissRequest = { finish() }
                )
            }
        }
    }

    private fun extractSelectedText(): String? {
        return when (intent.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            }
            Intent.ACTION_SEND -> {
                intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            }
            else -> null
        }?.trim()?.takeIf { it.isNotBlank() }
    }
}