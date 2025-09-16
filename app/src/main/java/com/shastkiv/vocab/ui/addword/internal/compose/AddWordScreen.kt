import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import com.shastkiv.vocab.ui.addword.compose.components.Content
import com.shastkiv.vocab.ui.addword.compose.state.AddWordUiState

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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFinish) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = stringResource(R.string.add_new_word),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(7.dp),
            shape = RoundedCornerShape(20.dp)
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


