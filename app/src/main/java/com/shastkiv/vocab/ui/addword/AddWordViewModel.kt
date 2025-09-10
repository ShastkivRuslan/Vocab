package com.shastkiv.vocab.ui.addword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.shastkiv.vocab.domain.repository.WordRepository
import com.shastkiv.vocab.domain.usecase.AddWordToDictionaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val languageRepository: LanguageRepository,
    private val addWordToDictionaryUseCase: AddWordToDictionaryUseCase
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _wordAdded = MutableLiveData(false)
    val wordAdded: LiveData<Boolean> = _wordAdded

    fun clearMessage() {
        _message.value = ""
    }

    val languageSettings: StateFlow<LanguageSettings> = languageRepository.languageSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LanguageSettings(
                appLanguage = Language("uk", "Українська", "🇺🇦"),
                targetLanguage = Language("uk", "Українська", "🇺🇦"),
                sourceLanguage = Language("en", "English", "🇬🇧")
            )
        )

    fun resetWordAdded() {
        _wordAdded.value = false
    }

    fun addWord(english: String, translation: String) {
        if (english.isEmpty() || translation.isEmpty()) {
            _message.value = "Заповніть усі поля"
            return
        }

        if (!english.matches("[a-zA-Z ]+".toRegex())) {
            _message.value = "Англійське слово повинно містити тільки латинські літери"
            return
        }

        if (!translation.matches("[а-яА-ЯіІїЇєЄґҐ' ]+".toRegex())) {
            _message.value = "Переклад повинен містити тільки українські літери"
            return
        }

        viewModelScope.launch {
            addWordToDictionaryUseCase(
                sourceWord = english,
                translation = translation,
                sourceLanguageCode = languageSettings.value.sourceLanguage.code,
                targetLanguageCode = languageSettings.value.targetLanguage.code,
                wordLevel = ""
            )
        }
    }
}