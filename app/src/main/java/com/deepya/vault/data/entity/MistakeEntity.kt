package com.deepya.vault.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "student_mistakes",
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["isResolved"]),
        Index(value = ["nextReviewTimestamp"])
    ]
)
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val studentId: String,          // Identifier for the student
    val questionId: Long,           // ID of the question answered incorrectly
    val questionStem: String,       // Text of the question missed
    val correctAnswer: String,      // The correct answer / key
    val studentAnswer: String,      // What the student answered

    // Spaced-Repetition & Remediation Tracking Fields
    val isResolved: Boolean = false,       // Becomes true once mastered
    val reviewCount: Int = 0,              // Tracks how many times it has been reviewed
    val nextReviewTimestamp: Long = 0L     // Epoch timestamp for 3 to 5 day re-test gating
)