package com.example.learnwordstrainer.ui.addword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWordViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _wordAdded = MutableLiveData(false)
    val wordAdded: LiveData<Boolean> = _wordAdded

    fun clearMessage() {
        _message.value = ""
    }



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
            if (wordRepository.wordExists(english)) {
                _message.postValue("Це слово вже існує в словнику")
                return@launch
            }

            wordRepository.addWord(english, translation)
            _message.postValue("Слово додано")
            _wordAdded.postValue(true)
        }
    }
}