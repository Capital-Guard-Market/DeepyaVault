package com.deepya.vault.data.model

import android.content.Context
import org.json.JSONObject

data class CurriculumQuestion(
    val id: String,
    val stem: String,
    val type: String,
    val marks: Int,
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val rubric: String = "",
    val difficulty: String = "MEDIUM",
    val isPyq: Boolean = false,
    val pyqYear: String = ""
)

data class CurriculumChapter(
    val chapterId: String,
    val chapterName: String,
    val questions: List<CurriculumQuestion>
)

data class CurriculumSubject(
    val subjectCode: String,
    val subjectTitle: String,
    val curriculum: String,
    val grade: Int,
    val chapters: List<CurriculumChapter>
)

object CurriculumLoader {

    fun loadAccountancy(context: Context): CurriculumSubject? {
        return try {
            val jsonString = context.assets
                .open("curriculum/cbse/commerce/class12/accountancy.json")
                .bufferedReader()
                .use { it.readText() }

            parseSubjectJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseSubjectJson(jsonString: String): CurriculumSubject {
        val root = JSONObject(jsonString)
        val subjectCode = root.optString("subjectCode", "ACC-055")
        val subjectTitle = root.optString("subjectTitle", "Accountancy")
        val curriculum = root.optString("curriculum", "CBSE")
        val grade = root.optInt("grade", 12)

        val chaptersList = mutableListOf<CurriculumChapter>()
        val chaptersArray = root.optJSONArray("chapters")

        if (chaptersArray != null) {
            for (i in 0 until chaptersArray.length()) {
                val chObj = chaptersArray.getJSONObject(i)
                val chapterId = chObj.optString("chapterId", "")
                val chapterName = chObj.optString("chapterName", "")

                val questionsList = mutableListOf<CurriculumQuestion>()
                val qArray = chObj.optJSONArray("questions")

                if (qArray != null) {
                    for (j in 0 until qArray.length()) {
                        val qObj = qArray.getJSONObject(j)

                        val options = mutableListOf<String>()
                        val optsArray = qObj.optJSONArray("options")
                        if (optsArray != null) {
                            for (k in 0 until optsArray.length()) {
                                options.add(optsArray.getString(k))
                            }
                        }

                        questionsList.add(
                            CurriculumQuestion(
                                id = qObj.optString("id", ""),
                                stem = qObj.optString("stem", ""),
                                type = qObj.optString("type", "MCQ"),
                                marks = qObj.optInt("marks", 1),
                                options = options,
                                correctAnswer = qObj.optString("correctAnswer", ""),
                                rubric = qObj.optString("rubric", ""),
                                difficulty = qObj.optString("difficulty", "MEDIUM"),
                                isPyq = qObj.optBoolean("isPyq", false),
                                pyqYear = qObj.optString("pyqYear", "")
                            )
                        )
                    }
                }

                chaptersList.add(
                    CurriculumChapter(
                        chapterId = chapterId,
                        chapterName = chapterName,
                        questions = questionsList
                    )
                )
            }
        }

        return CurriculumSubject(
            subjectCode = subjectCode,
            subjectTitle = subjectTitle,
            curriculum = curriculum,
            grade = grade,
            chapters = chaptersList
        )
    }
}