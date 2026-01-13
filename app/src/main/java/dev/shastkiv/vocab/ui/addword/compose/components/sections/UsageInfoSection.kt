package dev.shastkiv.vocab.ui.addword.compose.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.common.ExpandableCard
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun UsageInfoSection(
    context : String?,
    isExpanded: Boolean,
    isLocked: Boolean,
    onToggle: () -> Unit
) {
    ExpandableCard(
        isExpanded = isExpanded,
        onToggle = onToggle,
        title = stringResource(R.string.additional_info),
        showArrow = !isLocked,
        isLocked = isLocked
    ) {
        if (!isLocked && context != null) {
            ContextContent(context)
        }
    }
}

@Composable
private fun ContextContent(context: String) {
    Column {
        Text(
            modifier = Modifier.padding(top = MaterialTheme.appDimensions.extraSmallPadding),
            text = context,
            style = MaterialTheme.appTypography.cardTitleMedium,
            color = MaterialTheme.appColors.cardTitleText
        )
    }
}