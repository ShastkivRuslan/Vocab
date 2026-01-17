package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R

@Composable
fun AddWordCard(onClick: () -> Unit) {
    val card = NavigationCardData(
            title = stringResource(R.string.add_new_word_button),
            description = stringResource(R.string.add_card_description),
            icon = Icons.Default.Add
        )

    NavigationCard(
        card,
        onClick = onClick
    )
}

@Composable
fun QuizCard(onClick: () -> Unit) {
    val card = NavigationCardData(
        title = stringResource(R.string.repeat_mode),
        description = stringResource(R.string.repetition_card_description),
        icon = Icons.Default.Quiz
    )

    NavigationCard(
        card,
        onClick = onClick
    )
}

@Composable
fun AllWordsCard(onClick: () -> Unit) {
    val card = NavigationCardData(
        title = stringResource(R.string.all_words_btn),
        description = stringResource(R.string.all_words_card_description),
        icon = Icons.Default.Checklist
    )

    NavigationCard(
        card,
        onClick = onClick
    )
}

@Composable
fun PracticeCard(onClick: () -> Unit) {
    val card = NavigationCardData(
        title = stringResource(R.string.practice_title),
        description = stringResource(R.string.practice_subtitle),
        icon = Icons.Default.Repeat
    )

    NavigationCard(
        card,
        onClick = onClick
    )
}
