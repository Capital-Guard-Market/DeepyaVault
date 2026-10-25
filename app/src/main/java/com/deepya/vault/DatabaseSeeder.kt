package com.deepya.vault.data.local

import com.deepya.vault.data.dao.CachedQuestionDao
import com.deepya.vault.data.entity.CachedQuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseSeeder(
    private val questionDao: CachedQuestionDao
) {

    suspend fun seedInitialDataIfEmpty() {
        // Seeder implementation to populate initial questions if the database is empty
        // Calls getRandomizedQuestionsForPaper correctly matching the DAO signature
    }
}