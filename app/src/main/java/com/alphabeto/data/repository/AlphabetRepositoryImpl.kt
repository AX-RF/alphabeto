package com.alphabeto.data.repository

import com.alphabeto.data.local.AlphabetJsonDataSource
import com.alphabeto.data.local.LetterProgressDao
import com.alphabeto.data.local.LetterProgressEntity
import com.alphabeto.data.model.AlphabetLanguage
import com.alphabeto.data.model.Letter
import com.alphabeto.data.model.LetterWithProgress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AlphabetRepositoryImpl(
    private val jsonDataSource: AlphabetJsonDataSource,
    private val progressDao: LetterProgressDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AlphabetRepository {

    override fun observeLetters(language: AlphabetLanguage): Flow<List<LetterWithProgress>> {
        return flow {
            val letters = jsonDataSource.getLetters(language)
            ensureProgressEntries(language, letters)
            emitAll(
                progressDao.observeProgressByLanguage(language)
                    .map { progressList -> combineLetters(letters, progressList) }
            )
        }.flowOn(ioDispatcher)
    }

    override suspend fun getLetter(
        language: AlphabetLanguage,
        character: String
    ): LetterWithProgress? = withContext(ioDispatcher) {
        val letters = jsonDataSource.getLetters(language)
        val progress = progressDao.getProgress(LetterProgressEntity.buildKey(language, character))
        val letter = letters.firstOrNull { it.character == character } ?: return@withContext null
        LetterWithProgress(
            letter = letter,
            isCompleted = progress?.isCompleted ?: false,
            tracedStrokes = progress?.tracedStrokes ?: 0
        )
    }

    override suspend fun markLetterCompleted(letter: Letter, tracedStrokes: Int) {
        updateLetterProgress(letter, tracedStrokes, isCompleted = true)
    }

    override suspend fun updateLetterProgress(
        letter: Letter,
        tracedStrokes: Int,
        isCompleted: Boolean
    ) = withContext(ioDispatcher) {
        val existing = progressDao.getProgress(
            LetterProgressEntity.buildKey(
                letter.language,
                letter.character
            )
        )
        val entity =
            (existing ?: LetterProgressEntity.create(letter.language, letter.character)).copy(
                isCompleted = isCompleted,
                tracedStrokes = tracedStrokes,
                updatedAt = System.currentTimeMillis()
            )
        progressDao.insertOrUpdate(entity)
    }

    override suspend fun resetProgress(language: AlphabetLanguage) = withContext(ioDispatcher) {
        val letters = jsonDataSource.getLetters(language)
        val fresh = letters.map { LetterProgressEntity.create(language, it.character) }
        progressDao.insertAll(fresh)
    }

    private suspend fun ensureProgressEntries(language: AlphabetLanguage, letters: List<Letter>) {
        val existing = withContext(ioDispatcher) {
            progressDao.observeProgressByLanguage(language).first()
        }
        if (existing.size == letters.size) return
        val existingKeys = existing.map { it.letterKey }.toSet()
        val missing = letters.filter {
            LetterProgressEntity.buildKey(
                language,
                it.character
            ) !in existingKeys
        }
        if (missing.isEmpty()) return
        val defaults = missing.map { LetterProgressEntity.create(language, it.character) }
        progressDao.insertAll(defaults)
    }

    private fun combineLetters(
        letters: List<Letter>,
        progressList: List<LetterProgressEntity>
    ): List<LetterWithProgress> {
        val progressByLetter = progressList.associateBy { it.character }
        return letters.map { letter ->
            val progress = progressByLetter[letter.character]
            LetterWithProgress(
                letter = letter,
                isCompleted = progress?.isCompleted ?: false,
                tracedStrokes = progress?.tracedStrokes ?: 0
            )
        }
    }
}
