package dev.shastkiv.vocab.ui.allwords.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.domain.model.Example
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.ui.addword.compose.components.common.LevelBadge
import dev.shastkiv.vocab.ui.theme.appColors


@Composable
fun MainInfoItem(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    word:  WordData,
    showArrow: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.appColors.cardBackground,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.appColors.cardBorder,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Основна інформація",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold)
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
    }
}

//@Preview
//@Composable
//private fun Preview() {
//    val sampleWord = WordData(
//        originalWord = "Compose",
//        translation = "сучасний декларативний UI інструментарій",
//        transcription = "[kəmˈpoʊz]",
//        partOfSpeech = "noun",
//        level = "B2",
//        usageInfo = "\"Англійське слово 'reusability' є формальним технічним терміном, що найчастіше зустрічається в програмуванні (reusability of code), інженерії та екології (reusability of materials). Воно має нейтральний відтінок і підкреслює ефективність та економність. Типові словосполучення: 'design for reusability' (проєктувати з розрахунком на повторне використання)",
//        examples = listOf(
//            Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
//            Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
//            Example("Learning Compose is essential.", "Вивчення Compose є важливим.")
//        )
//    )
//    MainInfoItem(word = sampleWord)
//}