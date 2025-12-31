package com.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.shastkiv.vocab.R

@Composable
fun AddWordCard(onClick: () -> Unit) {
    val card = NavigationCardData(
            title = stringResource(R.string.add_new_word_button),
            description = stringResource(R.string.add_card_description),
            icon = Icons.Default.Add,
            colorGradient = listOf(Color(0x335EEAD4), Color(0x1A22D3EE)),
            iconBgColor = Color(0x335EEAD4),
            iconColor = Color(0xFF99F6E4)
        )

    NavigationCardItem(
        card,
        onClick = onClick
    )
}

@Composable
fun QuizCard(onClick: () -> Unit) {
    val card = NavigationCardData(
        title = stringResource(R.string.repeat_mode),
        description = stringResource(R.string.repetition_card_description),
        icon = Icons.Default.Quiz,
        colorGradient = listOf(Color(0x33C084FC), Color(0x1AC084FC)),
        iconBgColor = Color(0x33C084FC),
        iconColor = Color(0xFFC4B5FD) // violet-300
    )

    NavigationCardItem(
        card,
        onClick = onClick
    )
}

@Composable
fun AllWordsCard(onClick: () -> Unit) {
    val card = NavigationCardData(
        title = stringResource(R.string.all_words_btn),
        description = stringResource(R.string.all_words_card_description),
        icon = Icons.Default.Checklist,
        colorGradient = listOf(Color(0x3393C5FD), Color(0x1A60A5FA)),
        iconBgColor = Color(0x3393C5FD),
        iconColor = Color(0xFFA5B4FC)
    )

    NavigationCardItem(
        card,
        onClick = onClick
    )
}

@Composable
fun PracticeCard(onClick: () -> Unit) {
    val card = NavigationCardData(
        title = stringResource(R.string.practice_title),
        description = stringResource(R.string.practice_subtitle),
        icon = Icons.Default.Repeat,
        colorGradient = listOf(Color(0x33F472B6), Color(0x1AFB7185)),
        iconBgColor = Color(0x33F472B6),
        iconColor = Color(0xFFF9A8D4) // pink-300
    )

    NavigationCardItem(
        card,
        onClick = onClick
    )
}
