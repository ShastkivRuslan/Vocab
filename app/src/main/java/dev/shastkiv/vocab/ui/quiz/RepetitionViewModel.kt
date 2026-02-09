package dev.shastkiv.vocab.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.CategoryCounts
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.RepetitionData
import dev.shastkiv.vocab.domain.model.enums.ExerciseType
import dev.shastkiv.vocab.domain.model.enums.StatType
import dev.shastkiv.vocab.domain.model.enums.WordCategory
import dev.shastkiv.vocab.domain.usecase.GetCategoryCountsUseCase
import dev.shastkiv.vocab.domain.usecase.GetTodayStatsUseCase
import dev.shastkiv.vocab.domain.usecase.GetTrainingSessionUseCase
import dev.shastkiv.vocab.domain.usecase.GetWordByIdUseCase
import dev.shastkiv.vocab.domain.usecase.UpdateDailyStatsUseCase
import dev.shastkiv.vocab.domain.usecase.UpdateWordScoreUseCase
import dev.shastkiv.vocab.ui.quiz.compose.components.SegmentState
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.utils.TTSManager
import dev.shastkiv.vocab.utils.mapThrowableToUiError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepetitionViewModel @Inject constructor(
    private val getTrainingSessionUseCase: GetTrainingSessionUseCase,
    private val getCategoryCountsUseCase: GetCategoryCountsUseCase,
    private val updateWordScoreUseCase: UpdateWordScoreUseCase,
    private val updateDailyStatsUseCase: UpdateDailyStatsUseCase,
    private val getTodayStatsUseCase: GetTodayStatsUseCase,
    private val getWordByIdUseCase: GetWordByIdUseCase,
    private val ttsManager: TTSManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepetitionUiState>(
        value = RepetitionUiState.Lobby(
            dailyStats = null,
            wordsCategorySelected = WordCategory.INTELLIGENT,
            counts = CategoryCounts()
        )
    )
    val uiState: StateFlow<RepetitionUiState> = _uiState.asStateFlow()

    private val _dailyStats = MutableStateFlow<DailyStatistic?>(null)
    val dailyStats = _dailyStats.asStateFlow()

    private val _selectedCategory = MutableStateFlow(WordCategory.INTELLIGENT)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _categoryCounts = MutableStateFlow(CategoryCounts())
    val categoryCounts = _categoryCounts.asStateFlow()

    private val sessionQueue = mutableListOf<RepetitionData>()
    private var segmentStates = mutableListOf<SegmentState>()
    private var initialSessionSize = 0
    private var sessionCorrectAnswers = 0
    private var sessionWrongAnswers = 0
    private var totalMasteryEarned = 0

    private val wrongAnswersMap = mutableMapOf<Int, List<String>>()
    private val preparedMistakesData = mutableListOf<RepetitionData>()

    private var isProcessingAnswer = false

    init {
        loadDailyStats()

        viewModelScope.launch {
            getCategoryCountsUseCase().collect { counts ->
                _categoryCounts.value = counts
                if (_uiState.value is RepetitionUiState.Lobby) {
                    _uiState.value = RepetitionUiState.Lobby(
                        dailyStats = _dailyStats.value,
                        counts = counts,
                        wordsCategorySelected = _selectedCategory.value
                    )
                }
            }
        }
    }

    private fun loadDailyStats() {
        viewModelScope.launch(ioDispatcher) {
            getTodayStatsUseCase().collect { stats ->
                _dailyStats.value = stats

                val currentState = _uiState.value
                if (currentState is RepetitionUiState.Lobby) {
                    _uiState.value = RepetitionUiState.Lobby(
                        dailyStats = stats,
                        wordsCategorySelected = selectedCategory.value,
                        counts = _categoryCounts.value
                    )
                }
            }
        }
    }

    private fun startNewSession() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = RepetitionUiState.Loading
            try {
                val sessionWords = getTrainingSessionUseCase(
                    limit = 10,
                    category = selectedCategory.value
                )

                if (sessionWords.isNotEmpty()) {
                    sessionQueue.clear()
                    sessionQueue.addAll(sessionWords)
                    initialSessionSize = sessionWords.size
                    sessionCorrectAnswers = 0
                    sessionWrongAnswers = 0
                    totalMasteryEarned = 0
                    wrongAnswersMap.clear()
                    preparedMistakesData.clear()

                    segmentStates = MutableList(initialSessionSize) { SegmentState.IDLE }

                    showNextWord()
                } else {
                    _uiState.value = RepetitionUiState.Error(dev.shastkiv.vocab.domain.model.UiError.EmptyData)
                }
            } catch (e: Exception) {
                _uiState.value = RepetitionUiState.Error(mapThrowableToUiError(e))
            }
        }
    }

    fun onEvent(event: RepetitionEvent) {
        when (event) {
            is RepetitionEvent.OnStartSessionClicked -> startNewSession()
            is RepetitionEvent.OnAnswerSelected -> handleAnswer(event.index)
            is RepetitionEvent.OnNextWordClicked -> showNextWord()
            is RepetitionEvent.OnListenClicked -> playCurrentWordTts()
            is RepetitionEvent.OnCategoryChanged -> {
                _selectedCategory.value = event.category
                if (_uiState.value is RepetitionUiState.Lobby) {
                    _uiState.value = RepetitionUiState.Lobby(
                        _dailyStats.value,
                        _selectedCategory.value,
                        _categoryCounts.value
                    )
                }
            }
            is RepetitionEvent.OnReturnToLobby -> {
                _uiState.value = RepetitionUiState.Lobby(
                    dailyStats = _dailyStats.value,
                    wordsCategorySelected = _selectedCategory.value,
                    counts = _categoryCounts.value
                )
            }
            is RepetitionEvent.OnStartMistakesReview -> startMistakesReview()
        }
    }

    private fun handleAnswer(index: Int) {
        val currentState = _uiState.value as? RepetitionUiState.Content ?: return
        if (isProcessingAnswer || currentState.selectedAnswerIndex != null) return
        isProcessingAnswer = true

        val isCorrect = currentState.answerOptions[index] == currentState.word.translation
        val exercise = ExerciseType.QUIZ

        val newMastery = exercise.calculateNewScore(
            currentScore = currentState.word.masteryScore,
            isCorrect = isCorrect
        )

        val updatedWordForUi = currentState.word.copy(
            masteryScore = newMastery,
            correctAnswerCount = if (isCorrect) currentState.word.correctAnswerCount + 1 else currentState.word.correctAnswerCount,
            wrongAnswerCount = if (!isCorrect) currentState.word.wrongAnswerCount + 1 else currentState.word.wrongAnswerCount
        )

        segmentStates[currentState.currentStep] = if (isCorrect) {
            SegmentState.CORRECT
        } else {
            SegmentState.WRONG
        }

        _uiState.value = currentState.copy(
            selectedAnswerIndex = index,
            isAnswerCorrect = isCorrect,
            word = updatedWordForUi,
            segmentStates = segmentStates.toList()
        )

        viewModelScope.launch(ioDispatcher) {
            try {
                updateWordScoreUseCase(
                    wordId = currentState.word.id,
                    currentCorrectCount = currentState.word.correctAnswerCount,
                    currentWrongCount = currentState.word.wrongAnswerCount,
                    currentMastery = currentState.word.masteryScore,
                    wasAnswerCorrect = isCorrect,
                    exerciseType = exercise
                )

                updateDailyStatsUseCase(if (isCorrect) StatType.CORRECT_ANSWER else StatType.WRONG_ANSWER)

                if (isCorrect) {
                    sessionCorrectAnswers++
                    totalMasteryEarned += exercise.weight
                } else {
                    sessionWrongAnswers++
                    wrongAnswersMap[currentState.word.id] = currentState.answerOptions
                }

                _uiState.update { state ->
                    if (state is RepetitionUiState.Content) state.copy(dailyStats = _dailyStats.value) else state
                }

            } catch (e: Exception) {
                _uiState.value = RepetitionUiState.Error(mapThrowableToUiError(e))
            } finally {
                isProcessingAnswer = false
            }
        }
    }

    private fun showNextWord() {
        if (sessionQueue.isEmpty()) {
            _uiState.value = RepetitionUiState.SessionFinished(
                totalCorrect = sessionCorrectAnswers,
                totalWrong = sessionWrongAnswers,
                masteryEarned = totalMasteryEarned,
                hasWrongAnswers = wrongAnswersMap.isNotEmpty()
            )

            if (wrongAnswersMap.isNotEmpty()) {
                viewModelScope.launch(ioDispatcher) {
                    try {
                        val freshWords = wrongAnswersMap.map { (wordId, options) ->
                            val freshWord = getWordByIdUseCase(wordId)
                            RepetitionData(freshWord, options)
                        }
                        preparedMistakesData.clear()
                        preparedMistakesData.addAll(freshWords)
                    } catch (e: Exception) {
                    }
                }
            }
        } else {
            val nextData = sessionQueue.removeAt(0)
            val currentStep = initialSessionSize - sessionQueue.size - 1

            _uiState.value = RepetitionUiState.Content(
                word = nextData.word,
                answerOptions = nextData.options,
                currentStep = currentStep,
                totalSteps = initialSessionSize,
                segmentStates = segmentStates.toList(),
                dailyStats = _dailyStats.value
            )
        }
    }

    private fun startMistakesReview() {
        if (preparedMistakesData.isEmpty()) return

        sessionQueue.clear()
        sessionQueue.addAll(preparedMistakesData)
        initialSessionSize = preparedMistakesData.size
        sessionCorrectAnswers = 0
        sessionWrongAnswers = 0
        totalMasteryEarned = 0
        wrongAnswersMap.clear()
        preparedMistakesData.clear()

        segmentStates = MutableList(initialSessionSize) { SegmentState.IDLE }

        showNextWord()
    }

    private fun playCurrentWordTts() {
        val state = _uiState.value as? RepetitionUiState.Content ?: return
        ttsManager.speak(
            text = state.word.sourceWord,
            languageCode = state.word.sourceLanguageCode)
    }
}

sealed interface RepetitionEvent {
    data class OnAnswerSelected(val index: Int) : RepetitionEvent
    data class OnCategoryChanged(val category: WordCategory): RepetitionEvent

    object OnStartSessionClicked : RepetitionEvent
    object OnNextWordClicked : RepetitionEvent
    object OnListenClicked : RepetitionEvent
    object OnReturnToLobby : RepetitionEvent
    object OnStartMistakesReview : RepetitionEvent
}