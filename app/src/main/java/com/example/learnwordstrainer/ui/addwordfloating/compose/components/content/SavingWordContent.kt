package com.example.learnwordstrainer.ui.addwordfloating.compose.components.content

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.PrimaryButton
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.sections.*
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState

@Composable
fun SavingWordContent(
    state: AddWordUiState.SavingWord,
    isMainSectionExpanded: Boolean,
    isExamplesSectionExpanded: Boolean,
    isContextSectionExpanded: Boolean,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onContextToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(
            visible = state.shouldShowSections,
            exit = fadeOut(animationSpec = tween(500)) +
                    shrinkVertically(animationSpec = tween(600))
        ) {
            Column() {
                WordInfoSection(
                    word = state.word,
                    isExpanded = isMainSectionExpanded,
                    onToggle = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExamplesSection(
                    examples = state.word.examples,
                    isExpanded = isExamplesSectionExpanded,
                    onToggle = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContextSection(
                    context = state.word.context,
                    isExpanded = isContextSectionExpanded,
                    onToggle = {}
                )

                Spacer(modifier = Modifier.height(16.dp))


                PrimaryButton(
                    text = stringResource(R.string.add_to_vocab),
                    onClick = { }
                )
                }
            }
        }

        AnimatedVisibility(
            visible = state.shouldShowLoader,
            enter = fadeIn(animationSpec = tween(300)) +
                    expandVertically(animationSpec = tween(400))
        ) {
            SavingIndicator()
        }

        AnimatedVisibility(
            visible = state.shouldShowSuccess,
            enter = fadeIn(animationSpec = tween(400)) +
                    expandVertically(animationSpec = tween(500)) +
                    scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        ) {
            SuccessMessage()
        }
}

@Composable
private fun SavingIndicator() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Збереження слова...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SuccessMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Слово додано до словника!",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}