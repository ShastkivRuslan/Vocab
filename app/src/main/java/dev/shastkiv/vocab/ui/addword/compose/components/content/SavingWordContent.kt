package dev.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.common.PrimaryButton
import dev.shastkiv.vocab.ui.addword.compose.components.sections.ExamplesSection
import dev.shastkiv.vocab.ui.addword.compose.components.sections.UsageInfoSection
import dev.shastkiv.vocab.ui.addword.compose.components.sections.WordInfoSection
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SavingWordContent(
    state: AddWordUiState.SavingWord,
    isMainSectionExpanded: Boolean,
    isExamplesSectionExpanded: Boolean,
    isContextSectionExpanded: Boolean
) {
    val dimensions = MaterialTheme.appDimensions
    Column(verticalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing)) {
        AnimatedVisibility(
            visible = state.shouldShowSections,
            exit = fadeOut(animationSpec = tween(500)) +
                    shrinkVertically(animationSpec = tween(600))
        ) {
            Column() {
                WordInfoSection(
                    originalWord = state.word.originalWord,
                    translation = state.word.translation,
                    wordData = state.word,
                    isExpanded = isMainSectionExpanded,
                    isLocked = false,
                    onToggle = {}
                )

                Spacer(modifier = Modifier.height(dimensions.smallSpacing))

                ExamplesSection(
                    examples = state.word.examples,
                    isExpanded = isExamplesSectionExpanded,
                    isLocked = false,
                    onToggle = {}
                )

                Spacer(modifier = Modifier.height(dimensions.smallSpacing))

                UsageInfoSection(
                    context = state.word.usageInfo,
                    isExpanded = isContextSectionExpanded,
                    isLocked = false,
                    onToggle = {}
                )

                Spacer(modifier = Modifier.height(dimensions.mediumSpacing))


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
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.smallCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = colors.accentContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.mediumPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(dimensions.iconSizeMedium),
                strokeWidth = 3.dp,
                color = colors.accent
            )

            Spacer(modifier = Modifier.width(dimensions.smallSpacing))

            Text(
                text = stringResource(R.string.saving_in_process),
                style = MaterialTheme.appTypography.cardTitleMedium,
                fontWeight = FontWeight.Medium,
                color = colors.onAccent
            )
        }
    }
}

@Composable
private fun SuccessMessage() {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.smallCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = colors.accentSoft
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.mediumPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ), label = ""
            )

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = colors.greenSuccess,
                modifier = Modifier
                    .size(dimensions.iconSizeMedium)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )

            Spacer(modifier = Modifier.width(dimensions.smallSpacing))

            Text(
                text = stringResource(R.string.word_successfully_added),
                fontSize = dimensions.buttonTextSize,
                fontWeight = FontWeight.SemiBold,
                color = colors.textMain
            )
        }
    }
}