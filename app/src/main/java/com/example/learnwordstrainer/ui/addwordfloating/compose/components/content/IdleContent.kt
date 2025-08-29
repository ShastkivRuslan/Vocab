package com.example.learnwordstrainer.ui.addwordfloating.compose.components.content

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.*

@Composable
fun IdleContent(
    word: TextFieldValue,
    onWordChange: (TextFieldValue) -> Unit,
    onCheckClick: () -> Unit
) {
    WordInputField(
        value = word,
        onValueChange = onWordChange,
        onClear = { onWordChange(TextFieldValue("")) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    PrimaryButton(
        text = stringResource(R.string.add_word_button_text),
        onClick = onCheckClick,
        enabled = word.text.isNotEmpty()
    )

    Spacer(modifier = Modifier.height(16.dp))

    DescriptionText(text = stringResource(R.string.description_add_word_dialog))
}