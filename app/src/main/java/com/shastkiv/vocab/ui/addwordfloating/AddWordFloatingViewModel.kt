package com.shastkiv.vocab.ui.addwordfloating

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.shastkiv.vocab.domain.repository.ThemeRepository
import com.shastkiv.vocab.domain.usecase.AddWordToDictionaryUseCase
import com.shastkiv.vocab.domain.usecase.CheckIfWordExistsUseCase
import com.shastkiv.vocab.domain.usecase.GetWordInfoUseCase
import com.shastkiv.vocab.domain.usecase.TranslateUseCase
import com.shastkiv.vocab.ui.addwordfloating.compose.state.AddWordUiState
import com.shastkiv.vocab.ui.addwordfloating.compose.state.UserStatus
import com.shastkiv.vocab.utils.TTSManager
import com.shastkiv.vocab.utils.mapThrowableToUiError
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
    private val application: Application,
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val addWordToDictionaryUseCase: AddWordToDictionaryUseCase,
    private val checkIfWordExistsUseCase: CheckIfWordExistsUseCase,
    private val ttsManager: TTSManager,
    private val languageRepository: LanguageRepository,
    private val translateUseCase: TranslateUseCase,
    themeRepository: ThemeRepository
) : ViewModel() {

    // --- NEW: User Status ---
    // For demonstration, we hardcode the user as Free.
    // In a real app, you would get this from your authentication/subscription logic.
    private val _userStatus = MutableStateFlow<UserStatus>(UserStatus.Free)
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
        ttsManager.shutdown() }

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
        if (word.isBlank()) return

        viewModelScope.launch {
            _uiState.value = AddWordUiState.Loading

            // --- MODIFIED LOGIC ---
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
                isAlreadySaved = false, // In Free tier, we don't check this initially
                isMainSectionExpanded = true // Always expanded for Free
            )
        } catch (e: Exception) {
            val uiError = mapThrowableToUiError(e)
            _uiState.value = AddWordUiState.Error(uiError)
        }
    }

    private suspend fun fetchFullWordInfo(word: String) {
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
                userStatus = UserStatus.Premium,
                originalWord = word,
                wordData = wordInfo,
                simpleTranslation = wordInfo.translation, // Can use translation from full data
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
        if (currentState is AddWordUiState.Success && currentState.wordData != null) {
            viewModelScope.launch {
                _uiState.value = AddWordUiState.SavingWord(
                    word = currentState.wordData,
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
                    sourceWord = currentState.wordData.originalWord,
                    translation = currentState.wordData.translation,
                    sourceLanguageCode = sourceLangCode,
                    targetLanguageCode = targetLangCode,
                    wordLevel = currentState.wordData.level
                )

                _uiState.update { state ->
                    (state as? AddWordUiState.SavingWord)?.copy(
                        savingStep = AddWordUiState.SavingStep.Success
                    ) ?: state
                }

                delay(1200)

                _uiState.value = AddWordUiState.DialogShouldClose
            }
        } else if (currentState is AddWordUiState.Success && currentState.simpleTranslation != null) {
            // Logic for Free user to save the word with minimal info if needed
            Toast.makeText(application, "Збереження простого перекладу...", Toast.LENGTH_SHORT).show()
            // Here you could implement saving the word with only the simple translation
        }
    }

    // --- NEW FUNCTION ---
    fun onGetFullInfoClicked() {
        if (userStatus.value is UserStatus.Free) {
            _uiState.value = AddWordUiState.ShowPaywall
        } else {
            // For premium users, this button might not even be visible,
            // but if it is, we can re-fetch the data.
            onCheckWord()
        }
    }

    // --- NEW FUNCTION ---
    fun onPaywallDismissed() {
        // Return to the previous Success state when the paywall is dismissed
        onCheckWord()
    }


    fun onMainInfoToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                if (state.userStatus is UserStatus.Free) return@let it // Cannot be toggled in Free
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
                if (state.userStatus is UserStatus.Free) return@let it // Cannot be toggled in Free
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
                if (state.userStatus is UserStatus.Free) return@let it // Cannot be toggled in Free
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