package com.shastkiv.vocab.ui.practice.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.ExampleData

@Composable
fun AiChatView(
    examples: List<ExampleData>,
    isLoading: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Vocab AI")
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_loading))
                    LottieAnimation(composition, modifier = Modifier.size(80.dp))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(examples) { example ->
                        ChatMessageBubble(example = example)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(example: ExampleData) {
    Column {
        Text(text = example.exampleText, style = MaterialTheme.typography.bodyMedium)
        Text(text = example.translationText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}