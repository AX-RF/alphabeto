package com.alphabeto.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.LetterWithProgress
import com.alphabeto.domain.usecase.ObserveLettersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AlphabetViewModel(
    private val language: AlphabetLanguage,
    private val observeLettersUseCase: ObserveLettersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AlphabetUiState())
    val state: StateFlow<AlphabetUiState> = _state.asStateFlow()

    init {
        observeLetters()
    }

    private fun observeLetters() {
        viewModelScope.launch {
            observeLettersUseCase(language)
                .onStart { _state.value = _state.value.copy(isLoading = true, error = null) }
                .catch { throwable ->
                    _state.value =
                        _state.value.copy(isLoading = false, error = throwable.localizedMessage)
                }
                .collect { letters ->
                    _state.value = AlphabetUiState(
                        isLoading = false,
                        error = null,
                        letters = letters
                    )
                }
        }
    }
}

data class AlphabetUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val letters: List<LetterWithProgress> = emptyList()
)

class AlphabetViewModelFactory(
    private val language: AlphabetLanguage,
    private val observeLettersUseCase: ObserveLettersUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlphabetViewModel::class.java)) {
            return AlphabetViewModel(language, observeLettersUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}
