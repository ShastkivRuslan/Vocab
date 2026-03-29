package dev.shastkiv.vocab.ui.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.CategoryCounts
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.enums.ExerciseType
import dev.shastkiv.vocab.domain.model.enums.StatType
import dev.shastkiv.vocab.domain.model.enums.WordCategory
import dev.shastkiv.vocab.domain.usecase.GetCategoryCountsUseCase
import dev.shastkiv.vocab.domain.usecase.GetFlashCardSessionUseCase
import dev.shastkiv.vocab.domain.usecase.GetTodayStatsUseCase
import dev.shastkiv.vocab.domain.usecase.UpdateDailyStatsUseCase
import dev.shastkiv.vocab.domain.usecase.UpdateWordScoreUseCase
import dev.shastkiv.vocab.ui.flashcard.state.FlashCardUiState
import dev.shastkiv.vocab.ui.quiz.compose.components.SegmentState
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
class FlashCardViewModel @Inject constructor(
    private val getFlashCardSessionUseCase: GetFlashCardSessionUseCase,
    private val getCategoryCountsUseCase: GetCategoryCountsUseCase,
    private val updateWordScoreUseCase: UpdateWordScoreUseCase,
    private val updateDailyStatsUseCase: UpdateDailyStatsUseCase,
    private val getTodayStatsUseCase: GetTodayStatsUseCase,
    private val ttsManager: TTSManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlashCardUiState>(
        FlashCardUiState.Lobby(
            dailyStats = null,
            wordsCategorySelected = WordCategory.INTELLIGENT,
            counts = CategoryCounts()
        )
    )
    val uiState: StateFlow<FlashCardUiState> = _uiState.asStateFlow()

    private val _dailyStats = MutableStateFlow<DailyStatistic?>(null)
    private val _selectedCategory = MutableStateFlow(WordCategory.INTELLIGENT)
    private val _categoryCounts = MutableStateFlow(CategoryCounts())

    private val sessionQueue = ArrayDeque<Word>()
    private var segmentStates = mutableListOf<SegmentState>()
    private var initialSessionSize = 0
    private var sessionCorrectAnswers = 0
    private var sessionWrongAnswers = 0
    private var totalMasteryEarned = 0
    private var isProcessingAnswer = false

    private val wordRetryCount = mutableMapOf<Int, Int>()
    private val maxRetries = 2

    init {
        loadDailyStats()
        viewModelScope.launch {
            getCategoryCountsUseCase().collect { counts ->
                _categoryCounts.value = counts
                if (_uiState.value is FlashCardUiState.Lobby) {
                    _uiState.value = FlashCardUiState.Lobby(
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
                if (_uiState.value is FlashCardUiState.Lobby) {
                    _uiState.value = FlashCardUiState.Lobby(
                        dailyStats = stats,
                        wordsCategorySelected = _selectedCategory.value,
                        counts = _categoryCounts.value
                    )
                }
            }
        }
    }

    fun onEvent(event: FlashCardEvent) {
        when (event) {
            is FlashCardEvent.OnCategoryChanged -> {
                _selectedCategory.value = event.category
                if (_uiState.value is FlashCardUiState.Lobby) {
                    _uiState.value = FlashCardUiState.Lobby(
                        _dailyStats.value,
                        _selectedCategory.value,
                        _categoryCounts.value
                    )
                }
            }
            FlashCardEvent.OnStartSessionClicked -> startNewSession()
            FlashCardEvent.OnKnowClicked -> handleAnswer(isCorrect = true)
            FlashCardEvent.OnDontKnowClicked -> handleAnswer(isCorrect = false)
            FlashCardEvent.OnNextCardClicked -> showNextCard()
            FlashCardEvent.OnListenClicked -> playCurrentWordTts()
            FlashCardEvent.OnReturnToLobby -> {
                _uiState.value = FlashCardUiState.Lobby(
                    dailyStats = _dailyStats.value,
                    wordsCategorySelected = _selectedCategory.value,
                    counts = _categoryCounts.value
                )
            }
        }
    }

    private fun startNewSession() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = FlashCardUiState.Loading
            try {
                val words = getFlashCardSessionUseCase(
                    limit = 10,
                    category = _selectedCategory.value
                )
                if (words.isNotEmpty()) {
                    sessionQueue.clear()
                    sessionQueue.addAll(words)
                    initialSessionSize = words.size
                    sessionCorrectAnswers = 0
                    sessionWrongAnswers = 0
                    totalMasteryEarned = 0
                    wordRetryCount.clear()
                    segmentStates = MutableList(initialSessionSize) { SegmentState.IDLE }
                    showNextCard()
                } else {
                    _uiState.value = FlashCardUiState.Error(
                        dev.shastkiv.vocab.domain.model.UiError.EmptyData
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FlashCardUiState.Error(mapThrowableToUiError(e))
            }
        }
    }

    private fun handleAnswer(isCorrect: Boolean) {
        val currentState = _uiState.value as? FlashCardUiState.Content ?: return
        if (isProcessingAnswer || currentState.isAnswered) return
        isProcessingAnswer = true

        val exercise = ExerciseType.FLASHCARD
        val newMastery = exercise.calculateNewScore(
            currentScore = currentState.word.masteryScore,
            isCorrect = isCorrect
        )

        val updatedWord = currentState.word.copy(
            masteryScore = newMastery,
            correctAnswerCount = if (isCorrect) currentState.word.correctAnswerCount + 1
            else currentState.word.correctAnswerCount,
            wrongAnswerCount = if (!isCorrect) currentState.word.wrongAnswerCount + 1
            else currentState.word.wrongAnswerCount
        )

        segmentStates[currentState.currentStep] =
            if (isCorrect) SegmentState.CORRECT else SegmentState.WRONG

        _uiState.value = currentState.copy(
            word = updatedWord,
            isAnswered = true,
            isAnswerCorrect = isCorrect,
            segmentStates = segmentStates.toList()
        )

        if (!isCorrect) {
            val retries = wordRetryCount.getOrDefault(currentState.word.id, 0)
            if (retries < maxRetries) {
                wordRetryCount[currentState.word.id] = retries + 1
                sessionQueue.addLast(updatedWord)
            }
        }

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
                updateDailyStatsUseCase(
                    if (isCorrect) StatType.CORRECT_ANSWER else StatType.WRONG_ANSWER
                )
                if (isCorrect) {
                    sessionCorrectAnswers++
                    totalMasteryEarned += exercise.weight
                } else {
                    sessionWrongAnswers++
                }
                _uiState.update { state ->
                    if (state is FlashCardUiState.Content)
                        state.copy(dailyStats = _dailyStats.value)
                    else state
                }
            } catch (e: Exception) {
                _uiState.value = FlashCardUiState.Error(mapThrowableToUiError(e))
            } finally {
                isProcessingAnswer = false
            }
        }
    }

    private fun showNextCard() {
        if (sessionQueue.isEmpty()) {
            _uiState.value = FlashCardUiState.SessionFinished(
                totalCorrect = sessionCorrectAnswers,
                totalWrong = sessionWrongAnswers,
                masteryEarned = totalMasteryEarned
            )
            return
        }

        val nextWord = sessionQueue.removeFirst()
        val currentStep = (initialSessionSize - sessionQueue.size - 1)
            .coerceIn(0, initialSessionSize - 1)

        _uiState.value = FlashCardUiState.Content(
            word = nextWord,
            nextWords = sessionQueue.take(2),
            currentStep = currentStep,
            totalSteps = initialSessionSize,
            segmentStates = segmentStates.toList(),
            dailyStats = _dailyStats.value
        )
    }

    private fun playCurrentWordTts() {
        val state = _uiState.value as? FlashCardUiState.Content ?: return
        ttsManager.speak(
            text = state.word.sourceWord,
            languageCode = state.word.sourceLanguageCode
        )
    }
}