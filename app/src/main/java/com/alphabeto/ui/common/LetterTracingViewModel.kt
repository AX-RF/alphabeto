package com.alphabeto.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.Letter
import com.alphabeto.data.model.LetterWithProgress
import com.alphabeto.domain.usecase.GetLetterDetailsUseCase
import com.alphabeto.domain.usecase.UpdateLetterProgressUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.alphabeto.R

class LetterTracingViewModel(
    private val language: AlphabetLanguage,
    private val character: String,
    private val getLetterDetailsUseCase: GetLetterDetailsUseCase,
    private val updateLetterProgressUseCase: UpdateLetterProgressUseCase,
    private val applicationContext: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LetterTracingUiState(isLoading = true))
    val uiState: StateFlow<LetterTracingUiState> = _uiState.asStateFlow()

    private var currentLetter: Letter? = null
    private var currentProgress: LetterWithProgress? = null
    private var pendingUpdateJob: Job? = null

    init {
        refreshLetter()
    }

    fun onRepeatSoundRequested() {
        if (!_uiState.value.isSoundAvailable || _uiState.value.soundToPlay != null) return
        val soundRes = currentLetter?.soundResourceId ?: 0
        if (soundRes != 0) {
            _uiState.value = _uiState.value.copy(soundToPlay = soundRes)
        }
    }

    fun onClearRequested() {
        _uiState.value = _uiState.value.copy(shouldClearCanvas = true)
    }

    fun onRestartRequested() {
        val soundRes = currentLetter?.soundResourceId ?: 0
        _uiState.value = _uiState.value.copy(
            shouldClearCanvas = true,
            soundToPlay = if (soundRes != 0) soundRes else _uiState.value.soundToPlay
        )
    }

    fun onStrokeCompleted(strokes: Int, totalLength: Float) {
        currentLetter?.let { letter ->
            pendingUpdateJob?.cancel()
            pendingUpdateJob = viewModelScope.launch {
                val isCompleted = strokes >= MIN_STROKES_FOR_COMPLETION
                updateLetterProgressUseCase(letter, strokes, isCompleted = isCompleted)
                _uiState.value = _uiState.value.copy(
                    progressDescription = applicationContext.getString(
                        R.string.format_tracing_progress,
                        strokes,
                        totalLength.toInt()
                    ),
                    isCompleted = isCompleted
                )
            }
        }
    }

    fun onTracingCleared() {
        currentLetter?.let { letter ->
            pendingUpdateJob?.cancel()
            pendingUpdateJob = viewModelScope.launch {
                updateLetterProgressUseCase(letter, tracedStrokes = 0, isCompleted = false)
                _uiState.value = _uiState.value.copy(
                    progressDescription = applicationContext.getString(
                        R.string.format_tracing_progress_simple,
                        0
                    ),
                    isCompleted = false
                )
            }
        }
    }

    fun onSoundPlayed() {
        _uiState.value = _uiState.value.copy(soundToPlay = null)
    }

    fun onCanvasCleared() {
        _uiState.value = _uiState.value.copy(shouldClearCanvas = false)
    }

    private fun refreshLetter() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val letterWithProgress = getLetterDetailsUseCase(language, character)
            currentLetter = letterWithProgress?.letter
            currentProgress = letterWithProgress
            val sound = letterWithProgress?.letter?.soundResourceId ?: 0
            val title = letterWithProgress?.letter?.character ?: character
            _uiState.value = LetterTracingUiState(
                isLoading = false,
                letterTitle = title,
                progressDescription = describeProgress(letterWithProgress),
                soundToPlay = if (sound != 0) sound else null,
                isSoundAvailable = sound != 0,
                isCompleted = letterWithProgress?.isCompleted ?: false
            )
        }
    }

    private fun describeProgress(letterWithProgress: LetterWithProgress?): String {
        if (letterWithProgress == null) {
            return applicationContext.getString(R.string.format_tracing_progress_simple, 0)
        }
        val strokes = letterWithProgress.tracedStrokes
        return if (letterWithProgress.isCompleted) {
            applicationContext.getString(R.string.format_tracing_completed, strokes)
        } else {
            applicationContext.getString(R.string.format_tracing_progress_simple, strokes)
        }
    }

    companion object {
        private const val MIN_STROKES_FOR_COMPLETION = 3
    }
}

class LetterTracingViewModelFactory(
    private val language: AlphabetLanguage,
    private val character: String,
    private val getLetterDetailsUseCase: GetLetterDetailsUseCase,
    private val updateLetterProgressUseCase: UpdateLetterProgressUseCase,
    private val applicationContext: android.content.Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LetterTracingViewModel::class.java)) {
            return LetterTracingViewModel(
                language,
                character,
                getLetterDetailsUseCase,
                updateLetterProgressUseCase,
                applicationContext
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${'$'}modelClass")
    }
}


data class LetterTracingUiState(
    val isLoading: Boolean = false,
    val letterTitle: String = "",
    val progressDescription: String = "",
    val soundToPlay: Int? = null,
    val shouldClearCanvas: Boolean = false,
    val isSoundAvailable: Boolean = false,
    val isCompleted: Boolean = false
)
