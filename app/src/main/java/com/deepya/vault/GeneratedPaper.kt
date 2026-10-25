package com.deepya.vault

data class PaperBlueprint(
    val schoolOrInstitute: String = "",
    val examTitle: String = "",
    val subjectCode: String = "",
    val totalMarks: Int = 100,
    val durationMinutes: Int = 180
)

data class PaperQuestion(
    val qNumber: Int,
    val section: String,
    val questionText: String,
    val marks: Int,
    val options: List<String> = emptyList(),
    val markingScheme: String = "",
    val chapterId: String = ""
)

data class GeneratedPaper(
    val blueprint: PaperBlueprint,
    val sections: Map<String, List<PaperQuestion>>,
    val questions: List<PaperQuestion> // Direct flat list needed for live previews & swaps
)