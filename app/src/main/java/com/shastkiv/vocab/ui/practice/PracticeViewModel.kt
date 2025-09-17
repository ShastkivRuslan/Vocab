package com.shastkiv.vocab.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.domain.model.ExampleData
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.usecase.GetPracticeWordsUseCase
import com.shastkiv.vocab.utils.TTSManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val getPracticeWordsUseCase: GetPracticeWordsUseCase,
    // private val getAiExamplesUseCase: GetAiExamplesUseCase,
    private val ttsManager: TTSManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeUiState>(PracticeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var wordDeck: MutableList<Word> = mutableListOf()
    private val maxDeckSize = 3 // Максимальна кількість карт в колоді (видимих + наступні)

    init {
        loadWordDeck()
    }

    fun onEvent(event: PracticeEvent) {
        when (event) {
            PracticeEvent.OnNextWordClicked -> showNextWord()
            PracticeEvent.OnListenClicked -> {
//                (uiState.value as? PracticeUiState.Content)?.let {
//                    ttsManager.speak(it.currentWord.sourceWord)
//                }
            }
        }
    }

    private fun loadWordDeck() {
        viewModelScope.launch {
            _uiState.value = PracticeUiState.Loading
            val words = getPracticeWordsUseCase()
            if (words.isEmpty()) {
                _uiState.value = PracticeUiState.Empty("Ваш словник порожній. Додайте слова, щоб почати практику.")
            } else {
                wordDeck.clear() // Очищуємо, якщо перезавантажуємо
                wordDeck.addAll(words)
                showNextWord(isInitialLoad = true) // Передаємо флаг для початкового завантаження
            }
        }
    }

    private fun showNextWord(isInitialLoad: Boolean = false) {
        if (wordDeck.isEmpty()) {
            loadWordDeck() // Перезавантажуємо колоду, якщо закінчилася
            return
        }

        val nextWord = wordDeck.removeAt(0)

        // Додаємо поточне слово в кінець колоди, якщо це не початкове завантаження
        // та якщо колода ще не пуста. Це імітує "переміщення в кінець".
        if (!isInitialLoad && _uiState.value is PracticeUiState.Content) {
            // Отримаємо попереднє поточне слово, якщо воно було
            val previousCurrentWord = (_uiState.value as PracticeUiState.Content).currentWord
            wordDeck.add(previousCurrentWord)
        }

        // Беремо наступні `maxDeckSize - 1` слова для задніх карток
        val nextWordsInDeck = wordDeck.take(maxDeckSize - 1)

        _uiState.value = PracticeUiState.Content(
            currentWord = nextWord,
           examples = emptyList(), // Очищуємо приклади для нового слова
            isAiLoadingExamples = true
        )

        loadExamplesForWord(nextWord)
    }

    private fun loadExamplesForWord(word: Word) {
        viewModelScope.launch {
            delay(1500) // Імітація завантаження
            val examples = generateMockExamples(word)

            _uiState.update {
                (it as? PracticeUiState.Content)?.copy(
                    examples = examples,
                    isAiLoadingExamples = false
                ) ?: it
            }
        }
    }

    private fun generateMockExamples(word: Word): List<ExampleData> {
        return listOf(
            ExampleData(
                "The beauty of the sunset was ephemeral, lasting only a few minutes.",
                "Краса заходу сонця була ефемерною, триваючи лише кілька хвилин."
            ),
            ExampleData(
               "He dedicated his life to the pursuit of knowledge.",
                "Він присвятив своє життя гонитві за знаннями."
            ),
            ExampleData(
                "Her resilience in the face of adversity was an inspiration to us all.",
                "Її стійкість перед лицем негараздів була натхненням для всіх нас."
            ),
            ExampleData(
                "It's crucial to distinguish between fact and opinion.",
                "Вкрай важливо розрізняти факт та думку."
            ),
            ExampleData(
                "The novel provides a compelling narrative of historical events.",
                "Роман пропонує захоплюючу розповідь про історичні події."
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}