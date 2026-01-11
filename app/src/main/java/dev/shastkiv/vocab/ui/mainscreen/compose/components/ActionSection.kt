import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.ui.mainscreen.compose.components.AddWordCard
import dev.shastkiv.vocab.ui.mainscreen.compose.components.AllWordsCard
import dev.shastkiv.vocab.ui.mainscreen.compose.components.PracticeCard
import dev.shastkiv.vocab.ui.mainscreen.compose.components.QuizCard
import dev.shastkiv.vocab.ui.mainscreen.compose.components.SectionHeader
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun ActionSection(
    title: String,
    onAddWordClick: () -> Unit,
    onRepetitionClick: () -> Unit,
    onAllWordsClick: () -> Unit,
    onPracticeClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    Column(modifier = Modifier.padding(horizontal = dimensions.mediumSpacing)) {

        SectionHeader(title = title)
        Column (
            modifier = Modifier
        ) {
            AddWordCard(onClick = onAddWordClick)
            HorizontalDivider(modifier = Modifier.size(dimensions.smallSpacing))
            QuizCard(onClick = onRepetitionClick)
            HorizontalDivider(modifier = Modifier.size(dimensions.smallSpacing))
            AllWordsCard(onClick = onAllWordsClick)
            HorizontalDivider(modifier = Modifier.size(dimensions.smallSpacing))
            PracticeCard(onClick = onPracticeClick)
        }
    }
}