import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addword.compose.components.Content
import com.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import com.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun AddWordScreen(
    viewModel: AddWordViewModel,
    initialText: String?,
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputWord by viewModel.inputWord.collectAsState()

    LaunchedEffect(initialText) {
        if (!initialText.isNullOrBlank()) {
            viewModel.initialize(initialText)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AddWordUiState.DialogShouldClose) {
            viewModel.resetState()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Navigate",
                tint = MaterialTheme.customColors.cardTitleText,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onFinish() }
            )
            Text(
                text = stringResource(R.string.add_new_word),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.customColors.cardTitleText,
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.customColors.cardBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.customColors.cardBorder,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Content(
                uiState = uiState,
                inputWord = inputWord,
                onInputChange = viewModel::onInputChange,
                onCheckWord = viewModel::onCheckWord,
                onAddToVocabulary = viewModel::onAddWord,
                onGetFullInfo = viewModel::onGetFullInfoClicked,
                onTextToSpeech = viewModel::onTextToSpeech,
                onMainInfoToggle = viewModel::onMainInfoToggle,
                onExamplesToggle = viewModel::onExamplesToggle,
                onUsageInfoToggle = viewModel::ontUsageInfoToggle,
                onPaywallDismissed = viewModel::onPaywallDismissed,
                onSubscribe = onFinish,
                onDismiss = onFinish
            )
        }
    }
}


