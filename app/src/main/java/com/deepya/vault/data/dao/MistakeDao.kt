package com.deepya.vault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepya.vault.data.entity.MistakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordMistake(mistake: MistakeEntity)

    @Query("""
        SELECT * FROM student_mistakes 
        WHERE studentId = :studentId AND isResolved = 0 AND nextReviewTimestamp <= :currentTimestamp
        ORDER BY reviewCount ASC, nextReviewTimestamp ASC
    """)
    suspend fun getDueMistakesForReview(studentId: String, currentTimestamp: Long): List<MistakeEntity>

    @Query("SELECT * FROM student_mistakes WHERE studentId = :studentId AND isResolved = 0")
    fun getUnresolvedMistakesStream(studentId: String): Flow<List<MistakeEntity>>

    @Query("UPDATE student_mistakes SET isResolved = 1 WHERE id = :mistakeId")
    suspend fun resolveMistake(mistakeId: Long)

    @Query("UPDATE student_mistakes SET reviewCount = reviewCount + 1, nextReviewTimestamp = :newTimestamp WHERE id = :mistakeId")
    suspend fun rescheduleMistakeReview(mistakeId: Long, newTimestamp: Long)
}