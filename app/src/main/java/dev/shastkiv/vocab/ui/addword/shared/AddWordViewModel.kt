package dev.shastkiv.vocab.ui.addword.shared

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.domain.model.enums.WordType
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import dev.shastkiv.vocab.domain.repository.ThemeRepository
import dev.shastkiv.vocab.domain.usecase.AddWordToDictionaryUseCase
import dev.shastkiv.vocab.domain.usecase.CheckIfWordExistsUseCase
import dev.shastkiv.vocab.domain.usecase.GetWordInfoUseCase
import dev.shastkiv.vocab.domain.usecase.TranslateUseCase
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.compose.state.UserStatus
import dev.shastkiv.vocab.ui.permission.MicrophonePermissionActivity
import android.content.Intent
import dev.shastkiv.vocab.utils.PermissionResultBus
import dev.shastkiv.vocab.utils.STTManager
import dev.shastkiv.vocab.utils.STTResult
import dev.shastkiv.vocab.utils.TTSManager
import dev.shastkiv.vocab.utils.mapThrowableToUiError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddWordViewModel @AssistedInject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val addWordToDictionaryUseCase: AddWordToDictionaryUseCase,
    private val checkIfWordExistsUseCase: CheckIfWordExistsUseCase,
    private val ttsManager: TTSManager,
    private val languageRepository: LanguageRepository,
    private val translateUseCase: TranslateUseCase,
    private val analytics: FirebaseAnalytics,
    private val sttManager: STTManager,
    themeRepository: ThemeRepository,
    @ApplicationContext private val context: Context
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

    val isListening: StateFlow<Boolean> = sttManager.isListening
    val isReady: StateFlow<Boolean> = sttManager.isReady
    val rmsLevel: StateFlow<Float> = sttManager.rmsLevel

    init {
        viewModelScope.launch {
            sttManager.sttResult.collect { result ->
                when (result) {
                    is STTResult.Success -> {
                        _inputWord.value = TextFieldValue(result.text)
                        onCheckWord()
                    }
                    is STTResult.Empty,
                    is STTResult.Error -> {}
                }
            }
        }
        viewModelScope.launch {
            PermissionResultBus.results.collect { result ->
                if (result.permission == Manifest.permission.RECORD_AUDIO && result.granted) {
                    val settings = languageRepository.getLatestLanguageSettings()
                    sttManager.startListening(settings.sourceLanguage.code)
                }
            }
        }
    }

    val themeMode: StateFlow<Int> = themeRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

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

        val bundle = Bundle().apply { putString("word_value", word) }
        analytics.logEvent("ai_word_search", bundle)

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
            _uiState.value = AddWordUiState.Error(mapThrowableToUiError(e))
        }
    }

    private suspend fun fetchFullWordInfo(word: String) {
        val settings = languageRepository.getLatestLanguageSettings()
        getWordInfoUseCase(
            word = word,
            sourceLanguage = settings.sourceLanguage,
            targetLanguage = settings.targetLanguage
        ).onSuccess { wordInfo ->
            val isSaved = checkIfWordExistsUseCase(
                sourceWord = wordInfo.originalWord,
                sourceLanguageCode = settings.sourceLanguage.code
            )
            _uiState.value = AddWordUiState.Success(
                userStatus = UserStatus.Premium,
                originalWord = wordInfo.originalWord,
                wordData = wordInfo,
                simpleTranslation = wordInfo.translation,
                isAlreadySaved = isSaved
            )
        }.onFailure { error ->
            val uiError = mapThrowableToUiError(error)
            _uiState.value = if (uiError is UiError.InvalidWord || uiError is UiError.WrongLanguage) {
                AddWordUiState.Warning(uiError)
            } else {
                AddWordUiState.Error(uiError)
            }
        }
    }

    fun onTextToSpeech(word: String) {
        viewModelScope.launch {
            val settings = languageRepository.getLatestLanguageSettings()
            ttsManager.speak(text = word, languageCode = settings.sourceLanguage.code)
        }
    }

    /**
     * Перевіряє наявність дозволу RECORD_AUDIO.
     * - Є дозвіл → одразу запускає STT
     * - Немає дозволу → запускає MicrophonePermissionActivity і чекає результат через PermissionResultHolder
     */
    fun onSpeechToText() {
        if (sttManager.isListening.value) {
            sttManager.stopListening()
            return
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startSTT()
        } else {
            val intent = Intent(context, MicrophonePermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private fun startSTT() {
        viewModelScope.launch {
            val settings = languageRepository.getLatestLanguageSettings()
            sttManager.startListening(settings.sourceLanguage.code)
        }
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

    fun onPaywallDismissed() { onCheckWord() }

    fun onMainInfoToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                if (state.userStatus is UserStatus.Free) return@let it
                state.copy(
                    isMainSectionExpanded = !state.isMainSectionExpanded,
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
                state.copy(
                    isMainSectionExpanded = false,
                    isExamplesSectionExpanded = !state.isExamplesSectionExpanded,
                    isUsageInfoSectionExpanded = false
                )
            } ?: it
        }
    }

    fun ontUsageInfoToggle() {
        _uiState.update {
            (it as? AddWordUiState.Success)?.let { state ->
                if (state.userStatus is UserStatus.Free) return@let it
                state.copy(
                    isMainSectionExpanded = false,
                    isExamplesSectionExpanded = false,
                    isUsageInfoSectionExpanded = !state.isUsageInfoSectionExpanded
                )
            } ?: it
        }
    }

    fun onRetryToIdle() {
        _uiState.value = AddWordUiState.Idle(userStatus = _userStatus.value)
    }
}