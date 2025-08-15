package com.example.learnwordstrainer.ui.repetition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learnwordstrainer.ui.repetition.compose.RepetitionScreen
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepetitionActivity : ComponentActivity() {

    private val viewModel: RepetitionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LearnWordsTrainerTheme {
                RepetitionScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}