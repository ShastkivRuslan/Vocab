package com.shastkiv.vocab.ui.allwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.UiError
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.WordData
import com.shastkiv.vocab.domain.model.enums.WordType
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.shastkiv.vocab.domain.repository.WordRepository
import com.shastkiv.vocab.domain.usecase.GetWordInfoUseCase
import com.shastkiv.vocab.ui.allwords.compose.state.AllWordsUiState
import com.shastkiv.vocab.utils.mapThrowableToUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortType {
    BY_DATE_NEWEST,
    BY_DATE_OLDEST,
    ALPHABETICALLY_AZ,
    ALPHABETICALLY_ZA
}

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
    }

    val availableLanguages: List<Language> = AVAILABLE_LANGUAGES

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortType = MutableStateFlow(SortType.BY_DATE_NEWEST)
    val sortType: StateFlow<SortType> = _sortType

    private val _languageFilter = MutableStateFlow("all")
    val languageFilter: StateFlow<String> = _languageFilter

    private val _expandedWordId = MutableStateFlow<Int?>(null)
    val expandedWordId: StateFlow<Int?> = _expandedWordId

    private val _expandedWordDetails = MutableStateFlow<Result<WordData>?>(null)
    val expandedWordDetails: StateFlow<Result<WordData>?> = _expandedWordDetails

    private val _isDetailsLoading = MutableStateFlow(false)
    val isDetailsLoading: StateFlow<Boolean> = _isDetailsLoading

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AllWordsUiState> =
        _languageFilter.flatMapLatest { langCode ->
            wordRepository.getWords(langCode)
        }.combine(searchQuery) { words, query ->
            Pair(words, query)
        }.combine(sortType) { (allWords, query), sort ->
            try {
                if (allWords.isEmpty() && query.isBlank()) {
                    AllWordsUiState.Error(UiError.EmptyData)
                } else {
                    val sortedList = when (sort) {
                        SortType.BY_DATE_NEWEST -> allWords.sortedByDescending { it.addedAt }
                        SortType.BY_DATE_OLDEST -> allWords.sortedBy { it.addedAt }
                        SortType.ALPHABETICALLY_AZ -> allWords.sortedBy { it.sourceWord }
                        SortType.ALPHABETICALLY_ZA -> allWords.sortedByDescending { it.sourceWord }
                    }

                    val filteredList = if (query.isBlank()) {
                        sortedList
                    } else {
                        sortedList.filter {
                            it.sourceWord.contains(query, ignoreCase = true) ||
                                    it.translation.contains(query, ignoreCase = true)
                        }
                    }
                    AllWordsUiState.Success(filteredList)
                }
            } catch (e: Exception) {
                AllWordsUiState.Error(mapThrowableToUiError(e))
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AllWordsUiState.Loading
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AllWordsUiState.Loading
            )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortChanged(sortType: SortType) {
        _sortType.value = sortType
    }

    fun onLanguageFilterChanged(langCode: String) {
        _languageFilter.value = langCode
    }

    fun onWordDelete(word: Word) {
        viewModelScope.launch {
            wordRepository.deleteWord(word)
        }
    }

    fun onWordClicked(word: Word) {
        val currentlyExpandedId = _expandedWordId.value
        if (currentlyExpandedId == word.id) {
            _expandedWordId.value = null
            _expandedWordDetails.value = null
            return
        }

        _expandedWordId.value = word.id
        _expandedWordDetails.value = null

        if (word.wordType == WordType.PRO) {
            _isDetailsLoading.value = true
            viewModelScope.launch {
                val sourceLang = AVAILABLE_LANGUAGES.find { it.code == word.sourceLanguageCode }
                    ?: Language(word.sourceLanguageCode, word.sourceLanguageCode.uppercase(), "❓")
                val targetLang = AVAILABLE_LANGUAGES.find { it.code == word.targetLanguageCode }
                    ?: Language(word.targetLanguageCode, word.targetLanguageCode.uppercase(), "❓")

                val result = getWordInfoUseCase(word.sourceWord, sourceLang, targetLang)
                _expandedWordDetails.value = result
                _isDetailsLoading.value = false
            }
        } else {
            _expandedWordDetails.value = Result.success(
                WordData(
                    originalWord = word.sourceWord,
                    translation = word.translation,
                    transcription = "",
                    partOfSpeech = "",
                    level = "",
                    usageInfo = "",
                    examples = emptyList()
                )
            )
        }
    }
}