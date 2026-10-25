package com.deepya.vault.data.model

data class QuestionSlotConfig(
    val slotNumber: Int,
    val chapterId: String,
    val difficulty: String, // "MEDIUM", "HARD", "ELITE"
    val marks: Int,
    val questionMode: String, // "MCQ", "SHORT_ANSWER", "NUMERICAL", "CASE_STUDY"
    val sourceType: String,   // "PYQ", "SQP", "QUESTION_BANK", "ON_THE_FLY"
    val cambridgePaperType: String = "STANDARD" // For Cambridge component papers e.g., "PAPER_1_MCQ", "PAPER_2_STRUCTURED"
)