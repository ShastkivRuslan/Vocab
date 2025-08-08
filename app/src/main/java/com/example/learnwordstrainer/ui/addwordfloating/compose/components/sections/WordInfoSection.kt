package com.example.learnwordstrainer.ui.addwordfloating.compose.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnwordstrainer.domain.model.Example
import com.example.learnwordstrainer.domain.model.WordData
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.*

@Composable
fun WordInfoSection(
    word: WordData,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    ExpandableCard(
        isExpanded = isExpanded,
        onToggle = onToggle,
        title = if (isExpanded) "Основна інформація" else word.originalWord,
        showArrow = !isExpanded
    ) {
        WordInfoContent(word)
    }
}

@Composable
private fun WordInfoContent(word: WordData) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = word.originalWord,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = word.transcription,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            LevelBadge(level = word.level)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Переклад:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))


        Text(
            text = word.translation,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = word.partOfSpeech,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
@Preview
private fun Preview() {
    WordInfoSection(WordData(
        originalWord = "Compose",
        translation = "компоуз",
        transcription = "[kəmˈpoʊz]",
        partOfSpeech = "noun",
        level = "B2",
        context = "\"Англійське слово 'reusability' є формальним технічним терміном, що найчастіше зустрічається в програмуванні (reusability of code), інженерії та екології (reusability of materials). Воно має нейтральний відтінок і підкреслює ефективність та економність. Типові словосполучення: 'design for reusability' (проєктувати з розрахунком на повторне використання)",
        examples = listOf(
            Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
            Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
            Example("Learning Compose is essential.", "Вивчення Compose є важливим.")
        )),
        isExpanded = true,
        onToggle = {})
}
