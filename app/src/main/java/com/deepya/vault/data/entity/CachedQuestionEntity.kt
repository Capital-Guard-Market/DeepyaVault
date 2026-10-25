package com.deepya.vault.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cached_questions",
    indices = [
        Index(value = ["chapterId"]),
        Index(value = ["marks"]),
        Index(value = ["curriculum"]),
        Index(value = ["subject"]),
        Index(value = ["taxonomyLevel"]),
        Index(value = ["sourceType"])
    ]
)
data class CachedQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val curriculum: String,      // "CBSE", "ISC", "CAMBRIDGE", "JEE-ADVANCED", "NEET", "CLAT", "CA-FOUNDATION"
    val subject: String,         // "ACCOUNTANCY", "BIOLOGY", "PHYSICS", "LAW", etc.
    val grade: Int,              // 11, 12, or specific tier representation
    val chapterId: String,       // e.g., "ACC_12_PARTNERSHIP_01"

    val questionStem: String,    // The main question prompt/text
    val questionType: String,    // "MCQ", "SHORT_ANSWER", "NUMERICAL", "CASE_STUDY"
    val marks: Int,              // Allocated mark weightage (1, 2, 3, 4, 6, 8)

    val optionsJson: String? = null,    // JSON array of options if MCQ, null otherwise
    val correctAnswer: String,          // Key or final numerical figure
    val stepMarkingRubric: String,      // Comprehensive step-by-step marking scheme

    // --- Cambridge & Elite Command Control ---
    val commandWord: String = "State",  // Cambridge Standard: "Explain", "Evaluate", "Calculate", etc.
    val taxonomyLevel: String = "UNDERSTANDING", // "REMEMBERING", "APPLICATION", "ANALYSIS", "EVALUATION"
    val difficulty: String = "MEDIUM",           // "EASY", "MEDIUM", "HARD", "ELITE"

    // --- Elite Asset & Publishing Pipelines ---
    val diagramAssetPath: String? = null,        // Local asset path for high-res figures/tables
    val usageCount: Int = 0,                     // Tracks generation frequency
    val sourceType: String = "QUESTION_BANK",    // "QUESTION_BANK", "ON_THE_FLY", "TEACHER_CRAFTED"
    val isBookReady: Boolean = true,             // Flags elite items for next year's physical print vault
    val qualityRatingScore: Double = 10.0        // Enforces the 10/10 benchmark
)