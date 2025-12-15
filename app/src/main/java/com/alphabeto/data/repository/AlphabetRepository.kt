package com.alphabeto.data.repository

import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.Letter
import com.alphabeto.data.model.LetterWithProgress
import kotlinx.coroutines.flow.Flow

interface AlphabetRepository {
    fun observeLetters(language: AlphabetLanguage): Flow<List<LetterWithProgress>>

    suspend fun getLetter(language: AlphabetLanguage, character: String): LetterWithProgress?

    suspend fun markLetterCompleted(letter: Letter, tracedStrokes: Int)

    suspend fun updateLetterProgress(letter: Letter, tracedStrokes: Int, isCompleted: Boolean)

    suspend fun resetProgress(language: AlphabetLanguage)
}
