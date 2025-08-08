package com.example.learnwordstrainer.ui.mainscreen

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.data.repository.ThemeRepository
import com.example.learnwordstrainer.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private  val wordRepository: WordRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow(themeRepository.themeMode)
    val themeMode = _themeMode.asStateFlow()

    private val _totalWordsCount = MutableLiveData(0)
    val totalWordsCount: LiveData<Int> = _totalWordsCount

    private val _learnedPercentage = MutableLiveData(0)
    val learnedPercentage: LiveData<Int> = _learnedPercentage

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {

            val total = wordRepository.getWordCount()
            val learned = wordRepository.getLearnedWordsCount()

            _totalWordsCount.value = total
            _learnedPercentage.value = if (total > 0) (learned * 100) / total else 0
        }
    }

    fun updateTheme() {
        _themeMode.value = themeRepository.themeMode
        AppCompatDelegate.setDefaultNightMode(themeRepository.themeMode)
    }
}