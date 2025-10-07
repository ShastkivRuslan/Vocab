package com.shastkiv.vocab.ui.initialsetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.domain.usecase.GetInitialSetupStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppStartViewModel @Inject constructor(
   getInitialSetupStatusUseCase: GetInitialSetupStatusUseCase
) : ViewModel() {
    val isSetupCompleted = getInitialSetupStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
}