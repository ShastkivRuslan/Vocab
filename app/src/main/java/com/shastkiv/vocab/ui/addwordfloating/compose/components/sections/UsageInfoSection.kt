package com.shastkiv.vocab.ui.addwordfloating.compose.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.ui.addwordfloating.compose.components.common.ExpandableCard

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
        title = "Додаткова інформація",
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