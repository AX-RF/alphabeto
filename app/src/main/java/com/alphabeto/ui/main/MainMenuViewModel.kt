package com.alphabeto.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainMenuViewModel : ViewModel() {

    private val _events = MutableSharedFlow<MainMenuEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<MainMenuEvent> = _events.asSharedFlow()

    fun onArabicSelected() {
        _events.tryEmit(MainMenuEvent.NavigateToArabic)
    }

    fun onFrenchSelected() {
        _events.tryEmit(MainMenuEvent.NavigateToFrench)
    }
}

sealed class MainMenuEvent {
    data object NavigateToArabic : MainMenuEvent()
    data object NavigateToFrench : MainMenuEvent()
}
