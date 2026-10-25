package com.deepya.vault.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapter_packs",
    indices = [
        Index(value = ["curriculum", "gradeLevel", "subjectName"])
    ]
)
data class ChapterPackEntity(
    @PrimaryKey
    val chapterId: String,               // e.g. "CBSE_12_ACC_CH01", "NEET_BIO_CH05"
    val curriculum: String,              // "CBSE", "NEET", "ICSE", "CAMBRIDGE"
    val gradeLevel: Int,                 // 11, 12 (0 for competitive exams like NEET)
    val subjectName: String,             // "Accountancy", "Biology", "Informatics Practices"
    val chapterNumber: Int,              // 1, 2, 3...
    val title: String,                   // e.g. "Accounting for Partnership: Basic Concepts"
    val syllabusEdition: String = "2025-26 Rationalized", // Tracks current NCERT/Board alignment
    val questionCount: Int,              // Number of questions in this cloud pack
    val tokenCost: Int = 25,             // Token price to unlock
    val isDownloaded: Boolean = false,   // True when cached locally for offline paper generation
    val downloadedAtTimestamp: Long? = null,
    val packVersion: Int = 1             // Incremented if typos or rubrics update on the server
)