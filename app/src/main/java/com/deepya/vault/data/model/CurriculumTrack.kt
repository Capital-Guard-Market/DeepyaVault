package com.deepya.vault.data.model

sealed class CurriculumTrack {
    abstract val grade: Int

    data class SchoolBoard(
        val boardName: String, // "CBSE", "ISC", "Cambridge"
        val subject: String,
        override val grade: Int = 12, // 11 or 12
        val paperComponent: String? = null // e.g., "Paper 1: MCQ", "Paper 2: Structured" for Cambridge
    ) : CurriculumTrack()

    data class Competitive(
        val examName: String, // "CA Foundation", "CUET", "JEE Main", "NEET", "BITSAT", "IPMAT", "CLAT"
        val subject: String,
        override val grade: Int = 12
    ) : CurriculumTrack()
}

data class QuestionBankChapter(
    val chapterId: String,
    val chapterTitle: String,
    val subjectCode: String,
    val totalQuestions: Int, // 80-120+ for boards, 150-300+ for competitive
    val fileSizeKb: Int,
    val isDownloaded: Boolean = false,
    val localFilePath: String? = null
)

enum class AppGatewayMode {
    QUESTION_BANKS,
    TESTS
}