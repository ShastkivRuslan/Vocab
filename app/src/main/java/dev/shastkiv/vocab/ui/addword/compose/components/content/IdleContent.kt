package dev.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.common.DescriptionText
import dev.shastkiv.vocab.ui.addword.compose.components.common.PrimaryButton
import dev.shastkiv.vocab.ui.addword.compose.components.common.WordInputField
import dev.shastkiv.vocab.ui.addword.compose.state.UserStatus
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun IdleContent(
    word: TextFieldValue,
    onWordChange: (TextFieldValue) -> Unit,
    onCheckClick: () -> Unit,
    userStatus: UserStatus,
    isListening: Boolean = false,
    isReady: Boolean = false,
    rmsLevel: Float = 0f,
    onMicClick: () -> Unit = {}
) {
    val dimensions = MaterialTheme.appDimensions

    WordInputField(
        value = word,
        onValueChange = onWordChange,
        onClear = { onWordChange(TextFieldValue("")) },
        isListening = isListening,
        isReady = isReady,
        rmsLevel = rmsLevel,
        onMicClick = onMicClick
    )

    Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

    PrimaryButton(
        text = stringResource(R.string.add_word_button_text),
        onClick = onCheckClick,
        enabled = word.text.isNotEmpty() && !isListening
    )

    Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

    if (userStatus is UserStatus.Premium) {
        DescriptionText(text = stringResource(R.string.description_free_add_word))
    } else {
        DescriptionText(stringResource(R.string.description_pro_add_word))
    }
}