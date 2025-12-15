package com.alphabeto.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [LetterProgressEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class KidsLearningDatabase : RoomDatabase() {
    abstract fun letterProgressDao(): LetterProgressDao

    companion object {
        @Volatile
        private var INSTANCE: KidsLearningDatabase? = null

        fun getInstance(context: Context): KidsLearningDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): KidsLearningDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                KidsLearningDatabase::class.java,
                "kids_learning.db"
            ).fallbackToDestructiveMigration().build()
        }
    }
}
