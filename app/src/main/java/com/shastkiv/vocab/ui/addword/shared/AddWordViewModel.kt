package com.shastkiv.vocab.ui.addword.shared

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.model.WordData
import com.shastkiv.vocab.domain.model.WordType
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.shastkiv.vocab.domain.repository.ThemeRepository
import com.shastkiv.vocab.domain.usecase.AddWordToDictionaryUseCase
import com.shastkiv.vocab.domain.usecase.CheckIfWordExistsUseCase
import com.shastkiv.vocab.domain.usecase.GetWordInfoUseCase
import com.shastkiv.vocab.domain.usecase.TranslateUseCase
import com.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import com.shastkiv.vocab.ui.addword.compose.state.UserStatus
import com.shastkiv.vocab.utils.TTSManager
import com.shastkiv.vocab.utils.mapThrowableToUiError
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * AddWordViewModel - Complex UI state management for word dialog.
 * Uses AssistedFactory instead of @HiltViewModel for Service context compatibility.
 *
 * This class handles sophisticated UI state transitions, coordinates multiple UseCase,
 * and manages reactive state for Compose UI - justifying ViewModel pattern over simple UseCase.
 */
class AddWordViewModel @AssistedInject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val addWordToDictionaryUseCase: AddWordToDictionaryUseCase,
    private val checkIfWordExistsUseCase: CheckIfWordExistsUseCase,
    private val ttsManager: TTSManager,
    private val languageRepository: LanguageRepository,
    private val translateUseCase: TranslateUseCase,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): AddWordViewModel
    }

    private val _userStatus = MutableStateFlow<UserStatus>(UserStatus.Premium)
    private val userStatus = _userStatus.asStateFlow()

    private val _uiState = MutableStateFlow<AddWordUiState>(AddWordUiState.Idle(userStatus = userStatus.value))
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

    fun resetState() {
        _uiState.value = AddWordUiState.Idle(userStatus = userStatus.value)
        _inputWord.value = TextFieldValue("")
    }

    fun onCheckWord() {
        val word = _inputWord.value.text
        if (word.isBlank()) return

        viewModelScope.launch {
            _uiState.value = AddWordUiState.Loading

            when (userStatus.value) {
                is UserStatus.Free -> fetchSimpleTranslation(word)
                is UserStatus.Premium -> fetchFullWordInfo(word)
            }
        }
    }

    private suspend fun fetchSimpleTranslation(word: String) {
        try {
            val translation = translateUseCase(word)
            _uiState.value = AddWordUiState.Success(
                userStatus = UserStatus.Free,
                originalWord = word,
                wordData = null,
                simpleTranslation = translation,
                isAlreadySaved = false,
                isMainSectionExpanded = true
            )
        } catch (e: Exception) {
            val uiError = mapThrowableToUiError(e)
            _uiState.value = AddWordUiState.Error(uiError)
        }
    }

    private suspend fun fetchFullWordInfo(word: String) {
        getWordInfoUseCase(
            word = word,
            sourceLanguage = languageSettings.value.sourceLanguage,
            targetLanguage = languageSettings.value.targetLanguage
        ).onSuccess { wordInfo ->
            val isSaved = checkIfWordExistsUseCase(
                sourceWord = word,
                sourceLanguageCode = languageSettings.value.sourceLanguage.code
            )
            _uiState.value = AddWordUiState.Success(
                userStatus = UserStatus.Premium,
                originalWord = word,
                wordData = wordInfo,
                simpleTranslation = wordInfo.translation,
                isAlreadySaved = isSaved
            )
        }.onFailure { error ->
            val uiError = mapThrowableToUiError(error)
            _uiState.value = AddWordUiState.Error(uiError)
        }
    }

    fun onTextToSpeech(word: String) {
        ttsManager.speak(word)
    }

    fun onAddWord() {
        val currentState = _uiState.value

        if (currentState is AddWordUiState.Success) {
            viewModelScope.launch {
                val wordDataForUI = currentState.wordData ?: createBasicWordData(currentState)

                _uiState.value = AddWordUiState.SavingWord(
                    word = wordDataForUI,
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

                when (currentState.userStatus) {
                    is UserStatus.Premium -> {
                        addWordToDictionaryUseCase(
                            sourceWord = currentState.wordData!!.originalWord,
                            translation = currentState.wordData.translation,
                            sourceLanguageCode = settings.sourceLanguage.code,
                            targetLanguageCode = settings.targetLanguage.code,
                            wordType = WordType.PRO,
                            wordData = currentState.wordData
                        )
                    }

                    is UserStatus.Free -> {
                        addWordToDictionaryUseCase(
                            sourceWord = currentState.originalWord,
                            translation = currentState.simpleTranslation!!,
                            sourceLanguageCode = settings.sourceLanguage.code,
                            targetLanguageCode = settings.targetLanguage.code,
                            wordType = WordType.FREE,
                            wordData = null
                        )
                    }
                }

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

    private fun createBasicWordData(state: AddWordUiState.Success): WordData {
        return WordData(
            originalWord = state.originalWord,
            translation = state.simpleTranslation ?: "",
            transcription = "",
            partOfSpeech = "",
            level = "Unknown",
            usageInfo = "",
            examples = emptyList()
        )
    }

    fun onGetFullInfoClicked() {
        if (userStatus.value is UserStatus.Free) {
            _uiState.value = AddWordUiState.ShowPaywall
        } else {
            onCheckWord()
        }
    }

    fun onPaywallDismissed() {
        onCheckWord()
    }

    fun onMainInfoToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                if (state.userStatus is UserStatus.Free) return@let it
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
                if (state.userStatus is UserStatus.Free) return@let it
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
                if (state.userStatus is UserStatus.Free) return@let it
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
        // Handle error shown state
    }
}