package com.deepya.vault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepya.vault.data.entity.CachedQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedQuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: CachedQuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<CachedQuestionEntity>)

    @Query("SELECT * FROM cached_questions WHERE chapterId = :chapterId")
    fun getQuestionsByChapter(chapterId: String): Flow<List<CachedQuestionEntity>>

    @Query("SELECT id FROM cached_questions WHERE chapterId = :chapterId")
    suspend fun getUnresolvedQuestionIds(chapterId: String): List<Long>

    @Query("SELECT * FROM cached_questions WHERE id IN (:questionIds)")
    suspend fun getQuestionsByIds(questionIds: List<Long>): List<CachedQuestionEntity>

    @Query("""
        SELECT * FROM cached_questions 
        WHERE curriculum = :curriculum AND subject = :subject AND grade = :grade 
        ORDER BY RANDOM() LIMIT :limit
    """)
    suspend fun getRandomizedQuestionsForPaper(
        curriculum: String,
        subject: String,
        grade: Int,
        limit: Int
    ): List<CachedQuestionEntity>

    @Query("""
        SELECT * FROM cached_questions 
        WHERE curriculum = :curriculum AND subject = :subject AND taxonomyLevel IN ('APPLICATION', 'ANALYSIS', 'EVALUATION')
        ORDER BY RANDOM() LIMIT :limit
    """)
    suspend fun getHotQuestionsForPaper(
        curriculum: String,
        subject: String,
        limit: Int
    ): List<CachedQuestionEntity>

    // --- Original Teacher's Surgical Slot Builder & Book Vault ---
    @Query("""
        SELECT * FROM cached_questions 
        WHERE curriculum = :curriculum AND subject = :subject AND chapterId = :chapterId AND difficulty = :difficulty AND marks = :marks 
        LIMIT 1
    """)
    suspend fun getSpecificSlotQuestion(
        curriculum: String,
        subject: String,
        chapterId: String,
        difficulty: String,
        marks: Int
    ): CachedQuestionEntity?

    @Query("SELECT * FROM cached_questions WHERE isBookReady = 1 AND qualityRatingScore >= 10.0")
    suspend fun getBookReadyVaultQuestions(): List<CachedQuestionEntity>

    @Query("UPDATE cached_questions SET usageCount = usageCount + 1 WHERE id IN (:questionIds)")
    suspend fun incrementUsageCount(questionIds: List<Long>)

    // --- Added for Test Compilation Engine Slot Matching ---
    @Query("""
        SELECT * FROM cached_questions 
        WHERE curriculum = :curriculum 
          AND subject = :subject 
          AND chapterId = :chapterId
    """)
    suspend fun getQuestionsForSlot(
        curriculum: String,
        subject: String,
        chapterId: String
    ): List<CachedQuestionEntity>
}