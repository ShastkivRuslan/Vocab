package com.example.learnwordstrainer.ui.addwordfloating.compose.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.ExpandableCard

@Composable
fun ContextSection(
    context : String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    ExpandableCard(
        isExpanded = isExpanded,
        onToggle = onToggle,
        title = "Контекст використання",
        showArrow = true
    ) {
        ContextContent(context)
    }
}

@Composable
private fun ContextContent(context: String) {
    Column {
        Text(
            text = context,
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
private fun Prewiev() {
    ContextSection(context = "Англійське слово 'reusability' є формальним технічним терміном, що найчастіше зустрічається в програмуванні (reusability of code), інженерії та екології (reusability of materials). Воно має нейтральний відтінок і підкреслює ефективність та економність. Типові словосполучення: 'design for reusability' (проєктувати з розрахунком на повторне використання)",
        isExpanded = true,
        onToggle = {})
}