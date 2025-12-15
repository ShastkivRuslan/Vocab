package com.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun SetupHeader(
    onBackPressed: () -> Unit,
    title: String,
    subTitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Navigate",
            tint = MaterialTheme.customColors.cardTitleText,
            modifier = Modifier
                .size(48.dp)
                .clickable { onBackPressed() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.customColors.cardTitleText
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.customColors.cardDescriptionText
            )
        }
    }
}