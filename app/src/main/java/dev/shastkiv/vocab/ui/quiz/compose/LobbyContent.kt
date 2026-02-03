package dev.shastkiv.vocab.ui.quiz.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.enums.WordCategory
import dev.shastkiv.vocab.ui.quiz.compose.components.CompactCategoryCard
import dev.shastkiv.vocab.ui.quiz.compose.components.ProgressCard
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography


@Composable
fun LobbyContent(
    state: RepetitionUiState.Lobby,
    onStartClick: () -> Unit,
    onCategoryChange: (WordCategory) -> Unit
) {
    val colors = MaterialTheme.appColors
    val dims = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.dailyStats?.let { stats ->
            ProgressCard(
                wrongCount = stats.wrongAnswers,
                correctCount = stats.correctAnswers
            )
        }

        Spacer(modifier = Modifier.height(dims.mediumSpacing))

        Text(
            text = stringResource(R.string.choose_focus),
            style = typography.sectionHeader,
            color = colors.textMain,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dims.smallSpacing)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dims.smallSpacing)
        ) {
            WordCategory.entries.forEach { category ->
                val count = category.getCount(state.counts)

                CompactCategoryCard(
                    titleRes = category.titleRes,
                    descriptionRes = category.descriptionRes,
                    count = count,
                    isSelected = category == state.wordsCategorySelected,
                    onCardClick = {
                        if (count > 0) {
                            onCategoryChange(category)
                            onStartClick()
                        }
                    }
                )
            }
        }
    }
}
