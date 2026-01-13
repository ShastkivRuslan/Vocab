package dev.shastkiv.vocab.ui.addword.compose.components.sections

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.Example
import dev.shastkiv.vocab.ui.addword.compose.components.common.ExpandableCard
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

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
        Spacer(modifier = Modifier.height(MaterialTheme.appDimensions.smallSpacing))
        examples.forEach { example ->
            ExampleCard(example = example)
        }
    }
}

@Composable
fun ExampleCard(example: Example) {
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .padding(vertical = dimensions.extraSmallPadding)
            .background(
                color = colors.cardBackground,
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = 1.dp,
                color = colors.cardBorder,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Column(modifier = Modifier.padding(dimensions.smallPadding)) {
            Text(
                text = example.sentence,
                style = typography.cardDescriptionMedium,
                fontWeight = FontWeight.Medium,
                color = colors.cardTitleText
            )

            Spacer(Modifier.height(dimensions.microSpacing))

            Text(
                text = example.translation,
                style = typography.cardDescriptionMedium,
                color = colors.cardDescriptionText,
            )
        }
    }
}
