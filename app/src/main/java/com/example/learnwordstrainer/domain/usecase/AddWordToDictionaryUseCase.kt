package com.example.learnwordstrainer.domain.usecase

import android.health.connect.datatypes.units.Length
import android.widget.Toast
import com.example.learnwordstrainer.data.repository.WordRepository
import javax.inject.Inject

class AddWordToDictionaryUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(englishWord: String, translation: String) {
        if (englishWord.isNotBlank() && translation.isNotBlank()) {
            repository.addWord(englishWord, translation)
        }
    }
}