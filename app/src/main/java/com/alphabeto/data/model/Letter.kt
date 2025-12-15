package com.alphabeto.data.model

import androidx.annotation.RawRes

enum class AlphabetLanguage {
    ARABIC,
    FRENCH
}

data class Letter(
    val character: String,
    @RawRes val soundResourceId: Int,
    val unicodeValue: String,
    val language: AlphabetLanguage
)

data class LetterWithProgress(
    val letter: Letter,
    val isCompleted: Boolean,
    val tracedStrokes: Int
)
