package com.alphabeto.data.local

import androidx.room.TypeConverter
import com.alphabeto.data.model.AlphabetLanguage

class RoomConverters {
    @TypeConverter
    fun fromLanguage(language: AlphabetLanguage): String = language.name

    @TypeConverter
    fun toLanguage(value: String): AlphabetLanguage = AlphabetLanguage.valueOf(value)
}
