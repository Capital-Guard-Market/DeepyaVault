package com.deepya.vault.data.entity

data class GeneratedPaperEntity(
    val id: Long = System.currentTimeMillis(),
    val testTitle: String = "",
    val boardAndGrade: String = "",
    val subjectName: String = "",
    val totalMarks: Int = 0,
    val docxFilePath: String = "",
    val pdfFilePath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)