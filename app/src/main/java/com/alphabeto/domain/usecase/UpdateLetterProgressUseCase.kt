package com.alphabeto.domain.usecase

import com.alphabeto.data.model.Letter
import com.alphabeto.data.repository.AlphabetRepository

class UpdateLetterProgressUseCase(private val repository: AlphabetRepository) {
    suspend operator fun invoke(letter: Letter, tracedStrokes: Int, isCompleted: Boolean) {
        repository.updateLetterProgress(letter, tracedStrokes, isCompleted)
    }
}
