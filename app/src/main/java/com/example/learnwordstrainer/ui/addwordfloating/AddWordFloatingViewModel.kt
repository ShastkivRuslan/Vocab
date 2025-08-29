package com.example.learnwordstrainer.ui.addwordfloating

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.domain.model.Language
import com.example.learnwordstrainer.domain.model.LanguageSettings
import com.example.learnwordstrainer.domain.repository.LanguageRepository
import com.example.learnwordstrainer.domain.repository.ThemeRepository
import com.example.learnwordstrainer.domain.usecase.AddWordToDictionaryUseCase
import com.example.learnwordstrainer.domain.usecase.CheckIfWordExistsUseCase
import com.example.learnwordstrainer.domain.usecase.GetWordInfoUseCase
import com.example.learnwordstrainer.navigation.Screen
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState
import com.example.learnwordstrainer.utils.TTSManager
import com.example.learnwordstrainer.utils.mapThrowableToUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWordFloatingViewModel @Inject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val addWordToDictionaryUseCase: AddWordToDictionaryUseCase,
    private val checkIfWordExistsUseCase: CheckIfWordExistsUseCase,
    private val ttsManager: TTSManager,
    private val languageRepository: LanguageRepository,
    themeRepository: ThemeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddWordUiState>(AddWordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _inputWord = MutableStateFlow(TextFieldValue(""))
    val inputWord = _inputWord.asStateFlow()

    val themeMode: StateFlow<Int> = themeRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    private val languageSettings: StateFlow<LanguageSettings> = languageRepository.languageSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LanguageSettings(
                appLanguage = Language("uk", "Українська", "🇺🇦"),
                targetLanguage = Language("uk", "Українська", "🇺🇦"),
                sourceLanguage = Language("en", "English", "🇬🇧")
            )
        )


    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }

    fun onInputChange(newValue: TextFieldValue) {
        _inputWord.value = newValue
    }

    fun initialize(initialText: String? = null) {
        initialText?.let { text ->
            if (text.isNotBlank()) {
                _inputWord.value = TextFieldValue(text)
                onCheckWord()
            }
        }
    }

    fun onCheckWord() {
        val word = _inputWord.value.text
        viewModelScope.launch {
            _uiState.value = AddWordUiState.Loading

            getWordInfoUseCase(
                word =  word,
                sourceLanguage = languageSettings.value.sourceLanguage,
                targetLanguage = languageSettings.value.targetLanguage
            ).onSuccess { wordInfo ->
                    val isSaved = checkIfWordExistsUseCase(
                        sourceWord = word,
                        sourceLanguageCode = languageSettings.value.sourceLanguage.code
                    )

                    _uiState.value = AddWordUiState.Success(
                        word = wordInfo,
                        isAlreadySaved = isSaved
                    )
                }
                .onFailure { error ->
                    val uiError = mapThrowableToUiError(error)
                    _uiState.value = AddWordUiState.Error(uiError)
                }
        }
    }

    fun onTextToSpeech(word: String) {
        ttsManager.speak(word)
    }

    fun onAddWord() {
        val currentState = _uiState.value
        if (currentState is AddWordUiState.Success) {
            viewModelScope.launch {
                _uiState.value = AddWordUiState.SavingWord(
                    word = currentState.word,
                    isAlreadySaved = currentState.isAlreadySaved,
                    savingStep = AddWordUiState.SavingStep.CollapsingCards,
                    isMainSectionExpanded = currentState.isMainSectionExpanded,
                    isExamplesSectionExpanded = currentState.isExamplesSectionExpanded,
                    isUsageInfoSectionExpanded = currentState.isUsageInfoSectionExpanded
                )

                delay(100)

                _uiState.update { state ->
                    (state as? AddWordUiState.SavingWord)?.copy(
                        savingStep = AddWordUiState.SavingStep.Saving
                    ) ?: state
                }

                val settings = languageRepository.getLatestLanguageSettings()
                val sourceLangCode = settings.sourceLanguage.code
                val targetLangCode = settings.targetLanguage.code

                addWordToDictionaryUseCase(
                    sourceWord = currentState.word.originalWord,
                    translation = currentState.word.translation,
                    sourceLanguageCode = sourceLangCode,
                    targetLanguageCode = targetLangCode,
                    wordLevel = currentState.word.level
                )

                _uiState.update { state ->
                    (state as? AddWordUiState.SavingWord)?.copy(
                        savingStep = AddWordUiState.SavingStep.Success
                    ) ?: state
                }

                delay(1200)

                _uiState.value = AddWordUiState.DialogShouldClose
            }
        }
    }

    fun onMainInfoToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                val isOpening = !state.isMainSectionExpanded
                state.copy(
                    isMainSectionExpanded = isOpening,
                    isExamplesSectionExpanded = false,
                    isUsageInfoSectionExpanded = false
                )
            } ?: it
        }
    }

    fun onExamplesToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                val isOpening = !state.isExamplesSectionExpanded
                state.copy(
                    isMainSectionExpanded = false,
                    isExamplesSectionExpanded = isOpening,
                    isUsageInfoSectionExpanded = false
                )
            } ?: it
        }
    }

    fun ontUsageInfoToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                val isOpening = !state.isUsageInfoSectionExpanded
                state.copy(
                    isMainSectionExpanded = false,
                    isExamplesSectionExpanded = false,
                    isUsageInfoSectionExpanded = isOpening
                )
            } ?: it
        }
    }

    fun onErrorShown() {

    }
}