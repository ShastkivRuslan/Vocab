import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.Content
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun AddWordScreen(
    viewModel: AddWordViewModel,
    initialText: String?,
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputWord by viewModel.inputWord.collectAsState()

    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions

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
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Navigate",
                tint = colors.cardTitleText,
                modifier = Modifier
                    .size(dimensions.headerIconSize)
                    .clickable { onFinish() }
            )

            Spacer(modifier = Modifier.width(dimensions.mediumSpacing))

            Text(
                text = stringResource(R.string.add_new_word),
                style = typography.header,
                color = colors.cardTitleText,
                modifier = Modifier.weight(1f)
            )
        }

        LiquidGlassCard(modifier = Modifier.padding(horizontal = dimensions.mediumPadding)
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


