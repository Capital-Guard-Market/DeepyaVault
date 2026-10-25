package com.deepya.vault.engine

import com.deepya.vault.data.dao.CachedQuestionDao
import com.deepya.vault.data.entity.CachedQuestionEntity
import java.util.UUID

class TestCompiler(private val cachedQuestionDao: CachedQuestionDao) {

    /**
     * Compiles a complete test paper based on a teacher's blueprint configuration.
     * Enforces curriculum isolation, exact question type filtering, and taxonomic distribution.
     */
    suspend fun compileTest(blueprint: TestBlueprint): Result<CompiledTestPaper> {
        return try {
            val compiledSections = mutableListOf<CompiledTestSection>()
            var totalMarksCalculated = 0

            for (sectionBlueprint in blueprint.sections) {
                // 1. Fetch eligible candidate pool from local database matching curriculum, subject, and chapter
                val candidatePool = cachedQuestionDao.getQuestionsForSlot(
                    curriculum = blueprint.curriculumTrack,
                    subject = blueprint.subject,
                    chapterId = sectionBlueprint.chapterId
                )

                // 2. Filter dynamically based on specific slot requirements (e.g., Assertion-Reasoning or Case Studies)
                val filteredPool = candidatePool.filter { question ->
                    val matchesType = sectionBlueprint.allowedTypes.isEmpty() ||
                            question.questionType in sectionBlueprint.allowedTypes

                    val matchesTaxonomy = sectionBlueprint.allowedTaxonomyLevels.isEmpty() ||
                            question.taxonomyLevel in sectionBlueprint.allowedTaxonomyLevels

                    matchesType && matchesTaxonomy
                }

                // 3. Verify if we have enough questions in the local vault to fulfill the requirement
                if (filteredPool.size < sectionBlueprint.requiredCount) {
                    return Result.failure(
                        IllegalStateException("Insufficient questions in vault for chapter ${sectionBlueprint.chapterId}. " +
                                "Required: ${sectionBlueprint.requiredCount}, Available matching: ${filteredPool.size}")
                    )
                }

                // 4. Randomize and select the precise quota required for the section
                val selectedQuestions = filteredPool.shuffled().take(sectionBlueprint.requiredCount)
                val sectionMarks = selectedQuestions.sumOf { it.marks }

                compiledSections.add(
                    CompiledTestSection(
                        sectionTitle = sectionBlueprint.sectionTitle,
                        questions = selectedQuestions,
                        totalMarks = sectionMarks
                    )
                )
                totalMarksCalculated += sectionMarks
            }

            val finalPaper = CompiledTestPaper(
                paperId = UUID.randomUUID().toString(),
                title = blueprint.paperTitle,
                curriculumTrack = blueprint.curriculumTrack,
                subject = blueprint.subject,
                sections = compiledSections,
                totalMarks = totalMarksCalculated,
                createdAt = System.currentTimeMillis()
            )

            Result.success(finalPaper)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Supporting Blueprint and Output Data Models
data class TestBlueprint(
    val paperTitle: String,
    val curriculumTrack: String,
    val subject: String,
    val sections: List<TestSectionBlueprint>
)

data class TestSectionBlueprint(
    val sectionTitle: String,
    val chapterId: String,
    val requiredCount: Int,
    val allowedTypes: List<String> = emptyList(),
    val allowedTaxonomyLevels: List<String> = emptyList()
)

data class CompiledTestSection(
    val sectionTitle: String,
    val questions: List<CachedQuestionEntity>,
    val totalMarks: Int
)

data class CompiledTestPaper(
    val paperId: String,
    val title: String,
    val curriculumTrack: String,
    val subject: String,
    val sections: List<CompiledTestSection>,
    val totalMarks: Int,
    val createdAt: Long
)