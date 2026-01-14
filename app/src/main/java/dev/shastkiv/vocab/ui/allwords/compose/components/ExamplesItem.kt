package dev.shastkiv.vocab.ui.allwords.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import dev.shastkiv.vocab.ui.addword.compose.components.sections.ExampleCard
import dev.shastkiv.vocab.ui.theme.appColors

@Composable
fun ExamplesItem(
    examples: List<Example>
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
                shape = MaterialTheme.shapes.large
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Прикалади використання: ",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Column {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    examples.forEach { example ->
                        ExampleCard(example = example)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    val examples = listOf(
        Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
        Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
        Example("Learning Compose is essential.", "Вивчення Compose є важливим.")
    )

    ExamplesItem(examples)
}