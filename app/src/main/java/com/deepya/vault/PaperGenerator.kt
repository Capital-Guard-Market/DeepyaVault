package com.deepya.vault

import android.content.Context
import com.deepya.vault.data.model.CurriculumLoader
import com.deepya.vault.data.repository.QuestionRepository

object PaperGenerator {

    /**
     * Finds an alternate candidate question matching the target question's mark weight,
     * ensuring it isn't already active in the paper. Works dynamically across ALL subjects and grades.
     */
    fun getSwapCandidate(
        context: Context,
        targetQuestion: PaperQuestion,
        currentQuestions: List<PaperQuestion>,
        subject: String = "Accountancy",
        grade: Int = 12,
        repository: QuestionRepository? = null
    ): PaperQuestion {
        val currentTexts = currentQuestions.map { it.questionText }.toSet()
        val allAvailableQuestions = mutableListOf<PaperQuestion>()

        // 1. Try loading from CurriculumLoader if Accountancy Grade 12 is selected
        if (grade == 12 && (subject.contains("Accountancy", ignoreCase = true) || subject.contains("Accounts", ignoreCase = true))) {
            val loadedSubject = CurriculumLoader.loadAccountancy(context)
            var globalCounter = 1
            loadedSubject?.chapters?.forEach { chapter ->
                chapter.questions.forEach { q ->
                    allAvailableQuestions.add(
                        PaperQuestion(
                            qNumber = globalCounter++,
                            section = determineSectionForMarks(q.marks),
                            questionText = q.stem,
                            marks = q.marks,
                            options = q.options,
                            markingScheme = q.rubric,
                            chapterId = chapter.chapterId
                        )
                    )
                }
            }
        }

        // 2. Dynamic candidate pool generation for all other subjects & grades (Grade 11 & Grade 12)
        if (allAvailableQuestions.isEmpty()) {
            val dynamicCandidates = generateDynamicSubjectQuestions(subject, grade, targetQuestion.marks, targetQuestion.chapterId)
            allAvailableQuestions.addAll(dynamicCandidates)
        }

        // Filter for questions matching the exact mark weight that aren't already present in current paper
        val candidates = allAvailableQuestions.filter {
            it.marks == targetQuestion.marks && !currentTexts.contains(it.questionText)
        }

        if (candidates.isNotEmpty()) {
            return candidates.random()
        }

        // Fallback: Generate a fresh on-the-fly candidate tailored to subject, grade, and mark
        val gradeLabel = if (grade == 11) "Grade XI" else "Grade XII"
        return PaperQuestion(
            qNumber = targetQuestion.qNumber,
            section = targetQuestion.section,
            questionText = "[$subject $gradeLabel] Alternate conceptual problem evaluating core principles for ${targetQuestion.chapterId.ifEmpty { "Chapter" }}.",
            marks = targetQuestion.marks,
            options = if (targetQuestion.marks == 1) listOf("(A) Option 1", "(B) Option 2", "(C) Option 3", "(D) Option 4") else emptyList(),
            markingScheme = "[1] Mark for correct principle statement.\n[${targetQuestion.marks - 1}] Marks for step-by-step resolution.",
            chapterId = targetQuestion.chapterId
        )
    }

    private fun generateDynamicSubjectQuestions(
        subject: String,
        grade: Int,
        targetMarks: Int,
        chapterId: String
    ): List<PaperQuestion> {
        val gradeLabel = if (grade == 11) "Class XI" else "Class XII"
        val questions = mutableListOf<PaperQuestion>()

        val questionStems = when {
            subject.contains("Physics", ignoreCase = true) -> if (grade == 11) listOf(
                "Derive the kinematic equation v^2 = u^2 + 2as using calculus for $gradeLabel.",
                "Calculate the terminal velocity of a spherical rain drop falling through viscous air.",
                "Explain the first law of thermodynamics and state its physical limitations.",
                "Assertion (A): Work done in a closed path in a conservative field is zero.\nReason (R): Gravitational force is a non-conservative force."
            ) else listOf(
                "Derive the expression for electric field intensity due to a dipole under $gradeLabel syllabus.",
                "Calculate the net force experienced by a charged particle moving perpendicular to a magnetic field.",
                "Explain the principle and working of a transformer with a labeled schematic diagram.",
                "Assertion (A): Refractive index depends on wavelength.\nReason (R): Speed of light varies in different media."
            )

            subject.contains("Chemistry", ignoreCase = true) -> if (grade == 11) listOf(
                "Define electronegativity and discuss its variation across a period in the Periodic Table.",
                "Calculate the enthalpy of formation of methane using Hess's Law of Constant Heat Summation.",
                "Explain the geometrical isomerism shown by 2-butene with appropriate structures.",
                "Assertion (A): Cations are smaller than their parent atoms.\nReason (R): Loss of electron increases effective nuclear charge."
            ) else listOf(
                "Explain the mechanism of $gradeLabel nucleophilic substitution reaction.",
                "Calculate the molar conductivity at infinite dilution using Kohlrausch's law.",
                "Predict the major product formed when phenol reacts with dilute nitric acid.",
                "Assertion (A): Transition elements form colored complexes.\nReason (R): Unpaired d-electrons undergo d-d transitions."
            )

            subject.contains("Mathematics", ignoreCase = true) || subject.contains("Maths", ignoreCase = true) -> if (grade == 11) listOf(
                "Find the equation of the line passing through (2, 3) and perpendicular to 3x + 4y = 7.",
                "Solve the trigonometric equation sin(2x) + cos(x) = 0 for general solutions.",
                "Find the term independent of x in the expansion of (2x + 1/x)^10.",
                "Calculate the probability of drawing an ace or a king from a well-shuffled deck."
            ) else listOf(
                "Evaluate the integral ∫ 1/(x^2 + a^2) dx using standard $gradeLabel formulas.",
                "Find the shortest distance between two skew lines in vector form.",
                "Solve the system of linear equations using matrix inversion method.",
                "Calculate the probability of obtaining at least two heads in 5 coin tosses."
            )

            subject.contains("Business", ignoreCase = true) -> if (grade == 11) listOf(
                "Differentiate between Partnership Firm and Joint Stock Company on the basis of liability and management.",
                "Explain the primary objectives and role of Small Business Enterprises in rural India.",
                "Analyze the advantages of e-Business over Traditional Business models.",
                "Explain the different sources of equity and debt finance for a new enterprise."
            ) else listOf(
                "Explain the top 4 principles of management given by Henri Fayol under $gradeLabel curriculum.",
                "Differentiate between Money Market and Capital Market on the basis of instruments and duration.",
                "Analyze the impact of Demonetization on the Indian Business Environment.",
                "Explain the steps involved in the Staffing process of a growing commercial enterprise."
            )

            subject.contains("Economics", ignoreCase = true) -> if (grade == 11) listOf(
                "Calculate Arithmetic Mean and Standard Deviation for the given frequency distribution ($gradeLabel).",
                "Explain the Law of Diminishing Marginal Utility with the help of a numerical schedule.",
                "Distinguish between Change in Quantity Demanded and Change in Demand with diagrams.",
                "Discuss the features of Perfect Competition and explain how price is determined."
            ) else listOf(
                "Explain the concept of Inflationary Gap with the help of a well-labeled diagram ($gradeLabel).",
                "Calculate Net Domestic Product at Factor Cost (NDP_fc) using Income Method.",
                "Discuss the role of Central Bank as a Lender of Last Resort.",
                "Analyze the main causes of land degradation in India during post-independence era."
            )

            subject.contains("Accountancy", ignoreCase = true) || subject.contains("Accounts", ignoreCase = true) -> if (grade == 11) listOf(
                "Prepare a Double Column Cash Book with Cash and Bank columns for the month of April 2025.",
                "Pass rectifying journal entries for errors discovered before preparation of Trial Balance.",
                "Calculate Depreciation using Written Down Value Method at 10% p.a. for 3 years.",
                "Prepare Trading and Profit & Loss Account and Balance Sheet from the given Trial Balance."
            ) else listOf(
                "Calculate goodwill using Capitalisation of Average Profit method from the given financial figures.",
                "Pass journal entries for forfeiture and reissue of 500 equity shares issued at premium.",
                "Prepare Revaluation Account and Partners' Capital Accounts on admission of a new partner.",
                "Calculate Operating Ratio and Debt-to-Equity Ratio from the given financial metrics."
            )

            else -> listOf(
                "Critically analyze core analytical principles for $subject under $gradeLabel framework.",
                "Solve the multi-tier problem evaluating key operational concepts in $subject.",
                "Elaborate on the practical applications of the primary theorem in this module.",
                "Evaluate the given case study statement and justify your conclusions."
            )
        }

        var counter = 1
        questionStems.forEach { stem ->
            questions.add(
                PaperQuestion(
                    qNumber = counter++,
                    section = determineSectionForMarks(targetMarks),
                    questionText = stem,
                    marks = targetMarks,
                    options = if (targetMarks == 1) listOf("(A) Option A", "(B) Option B", "(C) Option C", "(D) Option D") else emptyList(),
                    markingScheme = "Standard $gradeLabel marking rubric key.",
                    chapterId = chapterId
                )
            )
        }

        return questions
    }

    private fun determineSectionForMarks(marks: Int): String {
        return when (marks) {
            1 -> "Section A - Objective Type"
            2 -> "Section B - Very Short Answer"
            3 -> "Section C - Short Answer"
            4 -> "Section D - Analytical Problems"
            else -> "Section E - Long Answer"
        }
    }
}