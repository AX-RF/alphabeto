package com.alphabeto.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alphabeto.data.model.AlphabetLanguage
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterProgressDao {
    @Query("SELECT * FROM letter_progress WHERE language = :language ORDER BY character ASC")
    fun observeProgressByLanguage(language: AlphabetLanguage): Flow<List<LetterProgressEntity>>

    @Query("SELECT * FROM letter_progress WHERE letter_key = :letterKey LIMIT 1")
    suspend fun getProgress(letterKey: String): LetterProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: LetterProgressEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(progressItems: List<LetterProgressEntity>)

    @Update
    suspend fun update(progress: LetterProgressEntity)
}
