package com.shastkiv.vocab.ui.addword.compose.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addword.compose.components.common.ExpandableCard

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
            text = context,
            fontSize = 18.sp
        )
    }
}