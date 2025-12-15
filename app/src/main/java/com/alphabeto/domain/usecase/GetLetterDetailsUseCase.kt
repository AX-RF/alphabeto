package com.alphabeto.domain.usecase

import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.LetterWithProgress
import com.alphabeto.data.repository.AlphabetRepository

class GetLetterDetailsUseCase(private val repository: AlphabetRepository) {
    suspend operator fun invoke(
        language: AlphabetLanguage,
        character: String
    ): LetterWithProgress? {
        return repository.getLetter(language, character)
    }
}
