package dev.shastkiv.vocab.ui.addword.shared

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddWordViewModelProvider @Inject constructor(
    private val addWordViewModelFactory: AddWordViewModel.Factory
) : ViewModel() {

    val addWordViewModel: AddWordViewModel by lazy {
        addWordViewModelFactory.create()
    }
}