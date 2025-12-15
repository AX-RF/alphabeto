package com.alphabeto.domain.usecase

import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.LetterWithProgress
import com.alphabeto.data.repository.AlphabetRepository
import kotlinx.coroutines.flow.Flow

class ObserveLettersUseCase(private val repository: AlphabetRepository) {
    operator fun invoke(language: AlphabetLanguage): Flow<List<LetterWithProgress>> {
        return repository.observeLetters(language)
    }
}
