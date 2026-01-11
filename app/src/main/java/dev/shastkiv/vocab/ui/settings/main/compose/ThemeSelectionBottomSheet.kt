package dev.shastkiv.vocab.ui.settings.main.compose

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.enums.ThemeMode
import dev.shastkiv.vocab.ui.settings.components.SettingsItemDivider
import dev.shastkiv.vocab.ui.theme.Black
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionBottomSheet(
    currentTheme: Int,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState
) {
    val themes = listOf(
        ThemeMode.SYSTEM to stringResource(R.string.theme_system),
        ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeMode.DARK to stringResource(R.string.theme_dark)
    )

    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth(),
        containerColor = colors.cardBackground,
        scrimColor = Black.copy(alpha = 0.32f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = colors.cardBorder
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = dimensions.extraLargeSpacing)
                .selectableGroup()
        ) {
            Text(
                text = stringResource(R.string.theme_selection_dialog_title),
                style = typography.sectionHeader,
                modifier = Modifier.padding(dimensions.mediumPadding),
                color = colors.textMain
            )

            SettingsItemDivider()

            themes.forEach { (themeMode, title) ->
                val isSelected = when (themeMode) {
                    ThemeMode.SYSTEM -> currentTheme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    ThemeMode.LIGHT -> currentTheme == AppCompatDelegate.MODE_NIGHT_NO
                    ThemeMode.DARK -> currentTheme == AppCompatDelegate.MODE_NIGHT_YES
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = { onThemeSelected(themeMode) }
                        )
                        .padding(
                            horizontal = dimensions.mediumPadding,
                            vertical = dimensions.smallPadding
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null
                    )
                    Text(
                        text = title,
                        style = typography.cardTitleMedium,
                        modifier = Modifier.padding(start = dimensions.mediumPadding),
                        color = colors.textMain
                    )
                }
            }
        }
    }
}