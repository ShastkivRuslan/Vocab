package com.shastkiv.vocab.ui.addword.compose.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.Example
import com.shastkiv.vocab.ui.addword.compose.components.common.ExpandableCard

@Composable
fun ExamplesSection(
    examples: List<Example>?,
    isExpanded: Boolean,
    isLocked: Boolean,
    onToggle: () -> Unit
) {
    ExpandableCard(
        isExpanded = isExpanded,
        onToggle = onToggle,
        title = stringResource(R.string.examples),
        showArrow = !isLocked,
        isLocked = isLocked
    ) {
        if (!isLocked && examples != null) {
            ExamplesContent(examples)
        }
    }
}

@Composable
private fun ExamplesContent(examples: List<Example>) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        examples.forEach { example ->
            ExampleCard(example = example)
        }
    }
}

@Composable
fun ExampleCard(example: Example) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = example.sentence,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = example.translation,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
