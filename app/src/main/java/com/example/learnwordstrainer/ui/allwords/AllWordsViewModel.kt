package com.example.learnwordstrainer.ui.allwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.domain.model.Language
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.model.WordData
import com.example.learnwordstrainer.domain.repository.LanguageRepository
import com.example.learnwordstrainer.domain.repository.WordRepository
import com.example.learnwordstrainer.domain.usecase.GetWordInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AllWordsUiState {
    object Loading : AllWordsUiState
    data class Success(val words: List<Word>) : AllWordsUiState
    data class Error(val message: String) : AllWordsUiState
}

enum class SortType {
    BY_DATE_NEWEST,
    BY_DATE_OLDEST,
    ALPHABETICALLY_AZ,
    ALPHABETICALLY_ZA
}

@HiltViewModel
class AllWordsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val languageRepository: LanguageRepository,
    private val getWordInfoUseCase: GetWordInfoUseCase // Ін'єктуємо UseCase
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

    val uiState: StateFlow<AllWordsUiState> =
        _languageFilter.flatMapLatest { langCode ->
            wordRepository.getWords(langCode)
        }.combine(searchQuery) { words, query ->
            Pair(words, query)
        }.combine(sortType) { (allWords, query), sort ->
            if (allWords.isEmpty() && query.isBlank()) {
                if (_languageFilter.value == "all") {
                    AllWordsUiState.Error("Ваш словник порожній.\nДодайте перше слово через плаваючу кнопку!")
                } else {
                    AllWordsUiState.Success(emptyList())
                }
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
        }
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
        } else {
            _expandedWordId.value = word.id
            _expandedWordDetails.value = null
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
        }
    }
}