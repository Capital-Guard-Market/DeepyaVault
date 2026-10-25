package com.deepya.vault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.deepya.vault.data.dao.CachedQuestionDao
import com.deepya.vault.data.dao.MistakeDao
import com.deepya.vault.data.dao.TokenDao
import com.deepya.vault.data.entity.CachedQuestionEntity
import com.deepya.vault.data.entity.MistakeEntity
import com.deepya.vault.data.entity.TokenLedgerEntity

@Database(
    entities = [
        CachedQuestionEntity::class,
        MistakeEntity::class,
        TokenLedgerEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cachedQuestionDao(): CachedQuestionDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "deepya_vault_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}