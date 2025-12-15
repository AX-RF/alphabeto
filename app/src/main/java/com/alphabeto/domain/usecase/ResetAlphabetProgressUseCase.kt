package com.alphabeto.domain.usecase

import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.repository.AlphabetRepository

class ResetAlphabetProgressUseCase(private val repository: AlphabetRepository) {
    suspend operator fun invoke(language: AlphabetLanguage) {
        repository.resetProgress(language)
    }
}
