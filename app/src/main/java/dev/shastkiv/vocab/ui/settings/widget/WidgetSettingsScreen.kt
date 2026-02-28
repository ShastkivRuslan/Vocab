package dev.shastkiv.vocab.ui.settings.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shastkiv.vocab.domain.model.WidgetClickAction
import dev.shastkiv.vocab.domain.model.WidgetFilterMode
import dev.shastkiv.vocab.ui.components.CustomSwitch
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.settings.components.SettingsHeader
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import dev.shastkiv.vocab.utils.WidgetHelper


@Composable
fun WidgetSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: WidgetSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WidgetSettingsEvent.RequestPinWidget -> {
                    WidgetHelper.requestPinWidget(context)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkWidgetStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    WidgetSettingsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onAddWidgetClick = viewModel::onAddWidgetClick,
        onUpdateFrequencyChange = viewModel::updateUpdateFrequency,
        onShowTranslationChange = viewModel::updateShowTranslation,
        onFilterModeChange = viewModel::updateFilterMode,
        onClickActionChange = viewModel::updateClickAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetSettingsContent(
    uiState: WidgetSettingsUiState,
    onBackClick: () -> Unit,
    onAddWidgetClick: () -> Unit,
    onUpdateFrequencyChange: (Int) -> Unit,
    onShowTranslationChange: (Boolean) -> Unit,
    onFilterModeChange: (WidgetFilterMode) -> Unit,
    onClickActionChange: (WidgetClickAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(
            onBackPressed = { onBackClick() },
            title = "Налаштування віджета",
            subTitle = "Налаштуй віджет"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState) {
                is WidgetSettingsUiState.Success -> {
                    if (uiState.isWidgetAdded) {
                        val settings = uiState.settings
                        if (settings != null) {
                            ShowTranslationCard(
                                isChecked = uiState.settings.showTranslation,
                                onCheckedChange = onShowTranslationChange
                            )

                            FrequencyCard(
                                current = uiState.settings.updateFrequencyMinutes,
                                onChange = onUpdateFrequencyChange
                            )

                            FilterModeCard(
                                current = uiState.settings.filterMode,
                                onChange = onFilterModeChange
                            )

                            ClickActionCard(
                                current = uiState.settings.clickAction,
                                onChange = onClickActionChange
                            )
                        }
                    } else {
                        AddWidgetPrompt(onClick = onAddWidgetClick)
                    }
                }

                WidgetSettingsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.appColors.accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowTranslationCard(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    LiquidGlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Показувати переклад",
                    color = colors.cardTitleText,
                    style = typography.cardTitleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Вимкніть, щоб тренувати пам'ять (переклад буде приховано)",
                    color = colors.cardDescriptionText,
                    style = typography.cardDescriptionSmall,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Використовуємо CustomSwitch компонент
            CustomSwitch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

// 2. CARD - Частота оновлення
@Composable
private fun FrequencyCard(
    current: Int,
    onChange: (Int) -> Unit
) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions

    val options = listOf(
        0 to "Off",
        15 to "15хв",
        30 to "30хв",
        60 to "1год",
        120 to "2год"
    )

    LiquidGlassCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Заголовок з акцентом
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Частота оновлення",
                    color = colors.cardTitleText,
                    style = dimensions.cardTitleMediumStyle,
                    fontWeight = FontWeight.SemiBold
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { (minutes, label) ->
                    val isSelected = current == minutes

                    // Плавна анімація кольору border з tween
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) colors.accent else colors.cardBorder,
                        animationSpec = tween(durationMillis = 300),
                        label = "freqBorderColor_$minutes"
                    )

                    // Плавна анімація кольору тексту
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) colors.accent else colors.textSecondary,
                        animationSpec = tween(durationMillis = 250),
                        label = "freqTextColor_$minutes"
                    )

                    // Анімація фону для вибраної кнопки
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) colors.cardBackground else colors.cardBackground,
                        animationSpec = tween(durationMillis = 300),
                        label = "freqBgColor_$minutes"
                    )

                    Surface(
                        onClick = { onChange(minutes) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        border = BorderStroke(
                            width = 1.dp,
                            color = borderColor
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterModeCard(
    current: WidgetFilterMode,
    onChange: (WidgetFilterMode) -> Unit
) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val dimensions = MaterialTheme.appDimensions

    LiquidGlassCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Заголовок з акцентом
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Які слова показувати?",
                    color = colors.cardTitleText,
                    style = typography.cardTitleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Grid 2x2
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterButton(
                        text = "Всі",
                        isSelected = current == WidgetFilterMode.ALL,
                        onClick = { onChange(WidgetFilterMode.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = "Вивчаю",
                        isSelected = current == WidgetFilterMode.LEARNING,
                        onClick = { onChange(WidgetFilterMode.LEARNING) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterButton(
                        text = "Вивчені",
                        isSelected = current == WidgetFilterMode.MASTERED,
                        onClick = { onChange(WidgetFilterMode.MASTERED) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterButton(
                        text = "Важкі",
                        isSelected = current == WidgetFilterMode.HARD,
                        onClick = { onChange(WidgetFilterMode.HARD) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// 4. CARD - При натисканні на віджет
@Composable
private fun ClickActionCard(
    current: WidgetClickAction,
    onChange: (WidgetClickAction) -> Unit
) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val dimensions = MaterialTheme.appDimensions

    LiquidGlassCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "При натисканні на віджет",
                    color = colors.cardTitleText,
                    style = typography.cardTitleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterButton(
                    text = "Відкрити",
                    isSelected = current == WidgetClickAction.OPEN_APP,
                    onClick = { onChange(WidgetClickAction.OPEN_APP) },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    text = "Наступне",
                    isSelected = current == WidgetClickAction.NEXT_WORD,
                    onClick = { onChange(WidgetClickAction.NEXT_WORD) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colors.accent else colors.cardBorder,
        animationSpec = tween(
            durationMillis = 350,
            delayMillis = 0
        ),
        label = "filterBorderColor"
    )

    // Плавна анімація кольору тексту з меншою затримкою
    val textColor by animateColorAsState(
        targetValue = if (isSelected) colors.accent else colors.textSecondary,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 50
        ),
        label = "filterTextColor"
    )

    // Анімація ширини border для ефекту "світіння"
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 1.5.dp else 1.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "filterBorderWidth"
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = colors.cardBackground,
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

// Add Widget Prompt (якщо віджет не додано)
@Composable
private fun AddWidgetPrompt(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вивчайте слова швидше!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Додайте віджет на головний екран, щоб бачити нові слова щоразу, коли берете телефон до рук.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Додати Віджет")
        }
    }
}