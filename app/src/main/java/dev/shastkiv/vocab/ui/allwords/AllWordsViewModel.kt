package dev.shastkiv.vocab.ui.allwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.domain.model.enums.WordType
import dev.shastkiv.vocab.domain.repository.WordRepository
import dev.shastkiv.vocab.domain.usecase.GetWordInfoUseCase
import dev.shastkiv.vocab.ui.allwords.compose.state.AllWordsUiState
import dev.shastkiv.vocab.utils.mapThrowableToUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ Events
sealed interface AllWordsEvent {
    data class SearchQueryChanged(val query: String) : AllWordsEvent
    data class SortTypeChanged(val sortType: SortType) : AllWordsEvent
    data class LanguageFilterChanged(val langCode: String) : AllWordsEvent
    data class WordClicked(val word: Word) : AllWordsEvent
    data class WordDeleted(val word: Word) : AllWordsEvent
}

enum class SortType {
    BY_DATE_NEWEST,
    BY_DATE_OLDEST,
    ALPHABETICALLY_AZ,
    ALPHABETICALLY_ZA
}

// ✅ Стани анімації
enum class AnimationPhase {
    IDLE,                    // Нічого не відбувається
    HIDING_OTHERS,          // Ховаємо інші слова + збільшуємо header
    MOVING_TO_TOP,          // Рухаємо фокусне слово вгору
    SHOWING_DETAILS,        // Показуємо деталі
    EXPANDED,               // Повністю розгорнуто
    HIDING_DETAILS,         // Ховаємо деталі
    MOVING_TO_ORIGINAL,     // Повертаємо слово на місце
    SHOWING_OTHERS          // Показуємо інші слова + зменшуємо header
}

data class ExpandedWordState(
    val wordId: Int,
    val originalIndex: Int,
    val animationPhase: AnimationPhase = AnimationPhase.IDLE,
    val isLoading: Boolean = false,
    val details: Result<WordData>? = null,
    val isDetailsVisible: Boolean = false
)

data class ScrollPosition(
    val index: Int = 0,
    val offset: Int = 0
)

@HiltViewModel
class AllWordsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val getWordInfoUseCase: GetWordInfoUseCase
) : ViewModel() {

    companion object {
        val AVAILABLE_LANGUAGES = listOf(
            Language("en", "English", "🇬🇧"),
            Language("de", "Deutsch", "🇩🇪"),
            Language("uk", "Українська", "🇺🇦"),
            Language("fr", "Français", "🇫🇷"),
            Language("pl", "Polski", "🇵🇱"),
            Language("cs", "Čeština", "🇨🇿")
        )

        const val HIDE_OTHERS_DURATION = 400L
        const val MOVE_TO_TOP_DURATION = 300L
        const val SHOW_DETAILS_DURATION = 400L
    }

    val availableLanguages: List<Language> = AVAILABLE_LANGUAGES

    private val _searchQuery = MutableStateFlow("")
    private val _sortType = MutableStateFlow(SortType.BY_DATE_NEWEST)
    private val _languageFilter = MutableStateFlow("all")
    private val _expandedWordState = MutableStateFlow<ExpandedWordState?>(null)
    private val _savedScrollPosition = MutableStateFlow(ScrollPosition())

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()
    val languageFilter: StateFlow<String> = _languageFilter.asStateFlow()
    val expandedWordState: StateFlow<ExpandedWordState?> = _expandedWordState.asStateFlow()
    val savedScrollPosition: StateFlow<ScrollPosition> = _savedScrollPosition.asStateFlow()

    val isDictionaryEmpty = wordRepository.getWordCount()
        .map { it == 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AllWordsUiState> = combine(
        _languageFilter.flatMapLatest { langCode ->
            wordRepository.getWords(langCode)
        },
        _searchQuery,
        _sortType
    ) { words, query, sort ->
        processWordsState(words, query, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AllWordsUiState.Loading
    )

    fun onEvent(event: AllWordsEvent) {
        when (event) {
            is AllWordsEvent.SearchQueryChanged -> handleSearchQueryChanged(event.query)
            is AllWordsEvent.SortTypeChanged -> handleSortTypeChanged(event.sortType)
            is AllWordsEvent.LanguageFilterChanged -> handleLanguageFilterChanged(event.langCode)
            is AllWordsEvent.WordClicked -> handleWordClicked(event.word)
            is AllWordsEvent.WordDeleted -> handleWordDeleted(event.word)
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun handleSortTypeChanged(sortType: SortType) {
        _sortType.value = sortType
    }

    private fun handleLanguageFilterChanged(langCode: String) {
        _languageFilter.value = langCode
    }

    private fun handleWordDeleted(word: Word) {
        viewModelScope.launch {
            wordRepository.deleteWord(word)
        }
    }

    private fun handleWordClicked(word: Word) {
        val currentState = _expandedWordState.value

        // Якщо клікнули на розгорнуте слово - закриваємо
        if (currentState?.wordId == word.id &&
            currentState.animationPhase == AnimationPhase.EXPANDED) {
            _expandedWordState.value = _expandedWordState.value?.copy(
                isDetailsVisible = false
            )
            closeExpandedWord()
            return
        }

        openWord(word)
    }

    private fun openWord(word: Word) {
        viewModelScope.launch {
            val currentWords = (uiState.value as? AllWordsUiState.Success)?.words ?: return@launch
            val originalIndex = currentWords.indexOfFirst { it.id == word.id }

            if (originalIndex == -1) return@launch

            _expandedWordState.value = ExpandedWordState(
                wordId = word.id,
                originalIndex = originalIndex,
                animationPhase = AnimationPhase.HIDING_OTHERS,
                isLoading = word.wordType == WordType.PRO
            )

            delay(HIDE_OTHERS_DURATION)

            _expandedWordState.value = _expandedWordState.value?.copy(
                animationPhase = AnimationPhase.MOVING_TO_TOP
            )

            delay(MOVE_TO_TOP_DURATION)

            _expandedWordState.value = _expandedWordState.value?.copy(
                animationPhase = AnimationPhase.SHOWING_DETAILS
            )

            if (word.wordType == WordType.PRO) {
                loadWordDetails(word)
            } else {
                _expandedWordState.value = _expandedWordState.value?.copy(
                    isLoading = false,
                    details = Result.success(createBasicWordData(word))
                )
            }

            delay(SHOW_DETAILS_DURATION)

            _expandedWordState.value = _expandedWordState.value?.copy(
                animationPhase = AnimationPhase.EXPANDED
            )
            _expandedWordState.value = _expandedWordState.value?.copy(
                isDetailsVisible = true
            )
        }
    }

    private fun closeExpandedWord() {
        viewModelScope.launch {
            _expandedWordState.value = _expandedWordState.value?.copy(
                isDetailsVisible = false,
                animationPhase = AnimationPhase.HIDING_DETAILS
            )

            delay(SHOW_DETAILS_DURATION)

            _expandedWordState.value = _expandedWordState.value?.copy(
                animationPhase = AnimationPhase.MOVING_TO_ORIGINAL
            )

            delay(MOVE_TO_TOP_DURATION)

            _expandedWordState.value = _expandedWordState.value?.copy(
                animationPhase = AnimationPhase.SHOWING_OTHERS
            )
            _expandedWordState.value = null
        }
    }

    private fun loadWordDetails(word: Word) {
        viewModelScope.launch {
            val sourceLang = AVAILABLE_LANGUAGES.find { it.code == word.sourceLanguageCode }
                ?: Language(word.sourceLanguageCode, word.sourceLanguageCode.uppercase(), "❓")
            val targetLang = AVAILABLE_LANGUAGES.find { it.code == word.targetLanguageCode }
                ?: Language(word.targetLanguageCode, word.targetLanguageCode.uppercase(), "❓")

            val result = getWordInfoUseCase(word.sourceWord, sourceLang, targetLang)

            _expandedWordState.value = _expandedWordState.value?.copy(
                isLoading = false,
                details = result
            )
        }
    }

    private fun createBasicWordData(word: Word) = WordData(
        originalWord = word.sourceWord,
        translation = word.translation,
        transcription = "",
        partOfSpeech = "",
        level = "",
        usageInfo = "",
        examples = emptyList()
    )

    private fun processWordsState(
        words: List<Word>,
        query: String,
        sort: SortType
    ): AllWordsUiState {
        return try {
            if (words.isEmpty()) {
                return if (isDictionaryEmpty.value) {
                    AllWordsUiState.Error(UiError.EmptyData)
                } else {
                    AllWordsUiState.Error(UiError.EmptyTargetLanguageWords)
                }
            }

            val processedWords = words
                .sortWords(sort)
                .filterWords(query)

            if (processedWords.isEmpty() && query.isNotBlank()) {
                return AllWordsUiState.Error(UiError.SearchError)
            }

            AllWordsUiState.Success(processedWords)

        } catch (e: Exception) {
            AllWordsUiState.Error(mapThrowableToUiError(e))
        }
    }

    private fun List<Word>.sortWords(sort: SortType): List<Word> = when (sort) {
        SortType.BY_DATE_NEWEST -> sortedByDescending { it.addedAt }
        SortType.BY_DATE_OLDEST -> sortedBy { it.addedAt }
        SortType.ALPHABETICALLY_AZ -> sortedBy { it.sourceWord }
        SortType.ALPHABETICALLY_ZA -> sortedByDescending { it.sourceWord }
    }

    private fun List<Word>.filterWords(query: String): List<Word> {
        if (query.isBlank()) return this
        return filter {
            it.sourceWord.contains(query, ignoreCase = true) ||
                    it.translation.contains(query, ignoreCase = true)
        }
    }

    fun saveScrollPosition(index: Int, offset: Int) {
        _savedScrollPosition.value = ScrollPosition(index, offset)
    }
}