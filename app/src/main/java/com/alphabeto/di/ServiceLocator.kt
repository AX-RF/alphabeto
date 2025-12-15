package com.alphabeto.di

import android.content.Context
import com.alphabeto.data.local.AlphabetJsonDataSource
import com.alphabeto.data.local.KidsLearningDatabase
import com.alphabeto.data.repository.AlphabetRepository
import com.alphabeto.data.repository.AlphabetRepositoryImpl
import com.alphabeto.domain.usecase.GetLetterDetailsUseCase
import com.alphabeto.domain.usecase.ObserveLettersUseCase
import com.alphabeto.domain.usecase.ResetAlphabetProgressUseCase
import com.alphabeto.domain.usecase.UpdateLetterProgressUseCase

object ServiceLocator {
    @Volatile
    private var repository: AlphabetRepository? = null

    fun provideAlphabetRepository(context: Context): AlphabetRepository {
        return repository ?: synchronized(this) {
            repository ?: buildRepository(context).also { repository = it }
        }
    }

    fun provideObserveLettersUseCase(context: Context): ObserveLettersUseCase {
        return ObserveLettersUseCase(provideAlphabetRepository(context))
    }

    fun provideGetLetterDetailsUseCase(context: Context): GetLetterDetailsUseCase {
        return GetLetterDetailsUseCase(provideAlphabetRepository(context))
    }

    fun provideUpdateLetterProgressUseCase(context: Context): UpdateLetterProgressUseCase {
        return UpdateLetterProgressUseCase(provideAlphabetRepository(context))
    }

    fun provideResetAlphabetProgressUseCase(context: Context): ResetAlphabetProgressUseCase {
        return ResetAlphabetProgressUseCase(provideAlphabetRepository(context))
    }

    private fun buildRepository(context: Context): AlphabetRepository {
        val database = KidsLearningDatabase.getInstance(context)
        val jsonDataSource = AlphabetJsonDataSource(context)
        return AlphabetRepositoryImpl(
            jsonDataSource = jsonDataSource,
            progressDao = database.letterProgressDao()
        )
    }
}
