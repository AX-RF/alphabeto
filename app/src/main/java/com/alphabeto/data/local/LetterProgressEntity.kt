package com.alphabeto.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alphabeto.data.model.AlphabetLanguage

@Entity(tableName = "letter_progress")
data class LetterProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "letter_key")
    val letterKey: String,
    @ColumnInfo(name = "language")
    val language: AlphabetLanguage,
    @ColumnInfo(name = "character")
    val character: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "traced_strokes")
    val tracedStrokes: Int,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
) {
    companion object {
        fun create(language: AlphabetLanguage, character: String): LetterProgressEntity {
            return LetterProgressEntity(
                letterKey = buildKey(language, character),
                language = language,
                character = character,
                isCompleted = false,
                tracedStrokes = 0,
                updatedAt = System.currentTimeMillis()
            )
        }

        fun buildKey(language: AlphabetLanguage, character: String): String =
            "${'$'}language-${'$'}character"
    }
}
