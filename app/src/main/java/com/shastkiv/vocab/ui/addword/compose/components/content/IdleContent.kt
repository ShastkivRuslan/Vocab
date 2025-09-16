package com.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addword.compose.components.common.*
import com.shastkiv.vocab.ui.addword.compose.state.UserStatus

@Composable
fun IdleContent(
    word: TextFieldValue,
    onWordChange: (TextFieldValue) -> Unit,
    onCheckClick: () -> Unit,
    userStatus: UserStatus
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

    val isPro = userStatus is UserStatus.Premium

    if (isPro) {
        DescriptionText(text = stringResource(R.string.description_free_add_word))
    } else {
        DescriptionText(stringResource(R.string.description_pro_add_word))
    }
}