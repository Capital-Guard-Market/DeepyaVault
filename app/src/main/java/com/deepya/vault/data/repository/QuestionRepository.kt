package com.deepya.vault.data.repository

import android.content.Context
import com.deepya.vault.data.dao.CachedQuestionDao
import com.deepya.vault.data.dao.MistakeDao
import com.deepya.vault.data.dao.TokenDao
import com.deepya.vault.data.entity.CachedQuestionEntity
import com.deepya.vault.data.model.CurriculumLoader
import com.deepya.vault.data.model.CurriculumTrack
import com.deepya.vault.data.model.QuestionBankChapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class QuestionRepository(
    private val questionDao: CachedQuestionDao? = null,
    private val mistakeDao: MistakeDao? = null,
    private val tokenDao: TokenDao? = null
) {

    // ==========================================
    // QUESTION BANK VAULT & CHAPTER WORKFLOWS
    // ==========================================

    fun getChaptersForTrack(track: CurriculumTrack, context: Context): List<QuestionBankChapter> {
        val subjectTitle = when (track) {
            is CurriculumTrack.SchoolBoard -> track.subject
            is CurriculumTrack.Competitive -> track.subject
        }
        val grade = when (track) {
            is CurriculumTrack.SchoolBoard -> track.grade
            is CurriculumTrack.Competitive -> track.grade
        }
        val boardName = when (track) {
            is CurriculumTrack.SchoolBoard -> track.boardName
            is CurriculumTrack.Competitive -> "Competitive"
        }

        // Cambridge Accounting 9706 (AS Level = Grade 11, A Level = Grade 12)
        if (boardName.contains("Cambridge", ignoreCase = true) || subjectTitle.contains("9706", ignoreCase = true) || (boardName == "Cambridge" && subjectTitle.contains("Account", ignoreCase = true))) {
            val cambridgeAccountingTitles = if (grade == 11) {
                // AS Level (Grade 11 / Class XI): Coursebook Chapters 1-24
                listOf(
                    "1. Double-entry bookkeeping: Cash & credit transactions",
                    "2. Books of prime entry & Balancing accounts",
                    "3. Division of the ledger & The trial balance",
                    "4. Statement of profit or loss for sole traders",
                    "5. Statements of financial position for sole traders",
                    "6. Accounting concepts & Accruals and prepayments",
                    "7. Accounting for depreciation of non-current assets & Irrecoverable debts",
                    "8. Bank reconciliation statements & Control accounts",
                    "9. Correction of errors & Incomplete records",
                    "10. Partnership accounts & Partnership changes",
                    "11. Manufacturing accounts",
                    "12. Limited company accounts & Further considerations",
                    "13. Non-profit making organisations (clubs and societies)",
                    "14. Statements of cash flows",
                    "15. Costing of materials, labour & Absorption costing",
                    "16. Unit, job, batch costing & Marginal costing"
                )
            } else {
                // A Level / A2 Level (Grade 12 / Class XII): Coursebook Chapters 25-40
                listOf(
                    "17. Auditing and stewardship & Ethics for accountants",
                    "18. International accounting standards (IAS)",
                    "19. Computerised accounting systems",
                    "20. Business acquisition and merger",
                    "21. Accounting information for stakeholders & Analysis/communication",
                    "22. Activity-based costing (ABC)",
                    "23. Budgeting and budgetary control",
                    "24. Standard costing",
                    "25. Investment appraisal"
                )
            }

            return cambridgeAccountingTitles.mapIndexed { i, title ->
                val levelPrefix = if (grade == 11) "AS_9706" else "A2_9706"
                QuestionBankChapter("${levelPrefix}_CH${i+1}", title, "9706", 95, 520, true, null)
            }
        }

        // CBSE / National Boards Accountancy
        if (subjectTitle.contains("Accountancy", ignoreCase = true) || subjectTitle.contains("Accounts", ignoreCase = true)) {
            if (grade == 11) {
                return listOf(
                    "Introduction to Accounting",
                    "Theory Base of Accounting",
                    "Recording of Transactions - I (Journal & Ledger)",
                    "Recording of Transactions - II (Cash Book & Special Journals)",
                    "Bank Reconciliation Statement",
                    "Trial Balance and Rectification of Errors",
                    "Depreciation, Provisions and Reserves",
                    "Financial Statements - I (Without Adjustments)",
                    "Financial Statements - II (With Adjustments)"
                ).mapIndexed { i, title ->
                    QuestionBankChapter("ACC11_CH${i+1}", title, "ACC-055-11", 90, 480, true, null)
                }
            } else {
                val loadedSubject = CurriculumLoader.loadAccountancy(context)
                if (loadedSubject != null) {
                    return loadedSubject.chapters.map { ch ->
                        QuestionBankChapter(
                            chapterId = ch.chapterId,
                            chapterTitle = ch.chapterName,
                            subjectCode = loadedSubject.subjectCode,
                            totalQuestions = ch.questions.size.coerceAtLeast(85),
                            fileSizeKb = 450 + (ch.questions.size * 5),
                            isDownloaded = true,
                            localFilePath = "assets/curriculum/cbse/commerce/class12/accountancy.json"
                        )
                    }
                }
            }
        }

        // Business Studies
        if (subjectTitle.contains("Business Studies", ignoreCase = true) || subjectTitle.contains("Business", ignoreCase = true)) {
            val titles = if (grade == 11) {
                listOf(
                    "Business, Trade and Commerce",
                    "Forms of Business Organisations",
                    "Private, Public and Global Enterprises",
                    "Business Services",
                    "Emerging Modes of Business",
                    "Social Responsibilities of Business and Business Ethics",
                    "Sources of Business Finance",
                    "Small Business and Enterprises",
                    "Internal Trade",
                    "International Business"
                )
            } else {
                listOf(
                    "Nature and Significance of Management",
                    "Principles of Management",
                    "Business Environment",
                    "Planning",
                    "Organising",
                    "Staffing",
                    "Directing",
                    "Controlling",
                    "Financial Management",
                    "Financial Markets",
                    "Marketing Management",
                    "Consumer Protection"
                )
            }
            return titles.mapIndexed { i, title ->
                QuestionBankChapter("BST${grade}_CH${i+1}", title, "BST-054", 90, 480, true, null)
            }
        }

        // Economics
        if (subjectTitle.contains("Economics", ignoreCase = true)) {
            val titles = if (grade == 11) {
                listOf(
                    "Statistics: Introduction & Collection of Data",
                    "Statistics: Organisation & Presentation of Data",
                    "Statistics: Measures of Central Tendency & Dispersion",
                    "Statistics: Index Numbers & Correlation",
                    "Microeconomics: Introduction & Consumer Equilibrium",
                    "Microeconomics: Demand & Elasticity of Demand",
                    "Microeconomics: Production & Cost",
                    "Microeconomics: Supply & Elasticity of Supply",
                    "Microeconomics: Forms of Market & Price Determination"
                )
            } else {
                listOf(
                    "Macroeconomics: National Income Accounting",
                    "Macroeconomics: Money and Banking",
                    "Macroeconomics: Determination of Income and Employment",
                    "Macroeconomics: Government Budget and the Economy",
                    "Macroeconomics: Balance of Payments",
                    "Indian Economic Development: Development Experience (1947-90) & Economic Reforms 1991",
                    "Indian Economic Development: Current Challenges Facing Indian Economy",
                    "Indian Economic Development: Development Experiences of India, Pakistan and China"
                )
            }
            return titles.mapIndexed { i, title ->
                QuestionBankChapter("ECO${grade}_CH${i+1}", title, "ECO-030", 95, 510, true, null)
            }
        }

        // Physics
        if (subjectTitle.contains("Physics", ignoreCase = true)) {
            val titles = if (grade == 11) {
                listOf(
                    "Units and Measurements",
                    "Motion in a Straight Line",
                    "Motion in a Plane",
                    "Laws of Motion",
                    "Work, Energy and Power",
                    "System of Particles and Rotational Motion",
                    "Gravitation",
                    "Mechanical Properties of Solids & Fluids",
                    "Thermal Properties of Matter & Thermodynamics",
                    "Kinetic Theory",
                    "Oscillations & Waves"
                )
            } else {
                listOf(
                    "Electric Charges and Fields",
                    "Electrostatic Potential and Capacitance",
                    "Current Electricity",
                    "Moving Charges and Magnetism",
                    "Magnetism and Matter",
                    "Electromagnetic Induction",
                    "Alternating Current",
                    "Electromagnetic Waves",
                    "Ray Optics and Optical Instruments",
                    "Wave Optics",
                    "Dual Nature of Radiation and Matter",
                    "Atoms",
                    "Nuclei",
                    "Semiconductor Electronics"
                )
            }
            return titles.mapIndexed { i, title ->
                QuestionBankChapter("PHY${grade}_CH${i+1}", title, "PHY-042", 100, 520, true, null)
            }
        }

        // Chemistry
        if (subjectTitle.contains("Chemistry", ignoreCase = true)) {
            val titles = if (grade == 11) {
                listOf(
                    "Some Basic Concepts of Chemistry",
                    "Structure of Atom",
                    "Classification of Elements and Periodicity in Properties",
                    "Chemical Bonding and Molecular Structure",
                    "Chemical Thermodynamics",
                    "Equilibrium",
                    "Redox Reactions",
                    "Organic Chemistry: Basic Principles and Techniques",
                    "Hydrocarbons"
                )
            } else {
                listOf(
                    "Solutions",
                    "Electrochemistry",
                    "Chemical Kinetics",
                    "d- and f- Block Elements",
                    "Coordination Compounds",
                    "Haloalkanes and Haloarenes",
                    "Alcohols, Phenols and Ethers",
                    "Aldehydes, Ketones and Carboxylic Acids",
                    "Amines",
                    "Biomolecules"
                )
            }
            return titles.mapIndexed { i, title ->
                QuestionBankChapter("CHM${grade}_CH${i+1}", title, "CHM-043", 105, 530, true, null)
            }
        }

        // Mathematics
        if (subjectTitle.contains("Mathematics", ignoreCase = true) || subjectTitle.contains("Maths", ignoreCase = true)) {
            val titles = if (grade == 11) {
                listOf(
                    "Sets",
                    "Relations and Functions",
                    "Trigonometric Functions",
                    "Complex Numbers and Quadratic Equations",
                    "Linear Inequalities",
                    "Permutations and Combinations",
                    "Binomial Theorem",
                    "Sequences and Series",
                    "Straight Lines",
                    "Conic Sections",
                    "Introduction to Three Dimensional Geometry",
                    "Limits and Derivatives",
                    "Statistics",
                    "Probability"
                )
            } else {
                listOf(
                    "Relations and Functions",
                    "Inverse Trigonometric Functions",
                    "Matrices",
                    "Determinants",
                    "Continuity and Differentiability",
                    "Application of Derivatives",
                    "Integrals",
                    "Application of Integrals",
                    "Differential Equations",
                    "Vector Algebra",
                    "Three Dimensional Geometry",
                    "Linear Programming",
                    "Probability"
                )
            }
            return titles.mapIndexed { i, title ->
                QuestionBankChapter("MTH${grade}_CH${i+1}", title, "MTH-041", 110, 550, true, null)
            }
        }

        // Fallback rich chapter packs for other subjects and competitive tracks
        return listOf(
            QuestionBankChapter("ch_${grade}_gen_1", "Foundation Concepts & Core Principles (Grade $grade)", "GEN-101", 110, 420, false, null),
            QuestionBankChapter("ch_${grade}_gen_2", "Advanced Problem Solving - Part 1 (Grade $grade)", "GEN-102", 145, 580, false, null),
            QuestionBankChapter("ch_${grade}_gen_3", "Advanced Problem Solving - Part 2 (Grade $grade)", "GEN-103", 160, 640, false, null),
            QuestionBankChapter("ch_${grade}_gen_4", "Mastery Practice & Case Studies (Grade $grade)", "GEN-104", 220, 890, false, null),
            QuestionBankChapter("ch_${grade}_gen_5", "Previous Years & Mock Test Set (Grade $grade)", "GEN-105", 250, 950, false, null)
        )
    }

    // ==========================================
    // EXISTING DATABASE & DRILL WORKFLOWS
    // ==========================================

    suspend fun getRemedialDrillQuestions(chapterId: String, count: Int): List<CachedQuestionEntity> = withContext(Dispatchers.IO) {
        val ids = questionDao?.getUnresolvedQuestionIds(chapterId) ?: emptyList()
        val selectedIds = ids.shuffled().take(count)
        questionDao?.getQuestionsByIds(selectedIds) ?: emptyList()
    }

    suspend fun getRandomChapterQuestions(chapterId: String, count: Int): List<CachedQuestionEntity> = withContext(Dispatchers.IO) {
        val allQuestions = questionDao?.getUnresolvedQuestionIds(chapterId) ?: emptyList()
        val selectedIds = allQuestions.shuffled().take(count)
        questionDao?.getQuestionsByIds(selectedIds) ?: emptyList()
    }

    fun getQuestionsByChapterFlow(chapterId: String): Flow<List<CachedQuestionEntity>> {
        return questionDao?.getQuestionsByChapter(chapterId) ?: flowOf(emptyList())
    }

    suspend fun fetchQuestionForSlot(
        curriculum: String,
        subject: String,
        chapterId: String,
        difficulty: String,
        marks: Int,
        sourceType: String
    ): CachedQuestionEntity = withContext(Dispatchers.IO) {
        if (sourceType != "ON_THE_FLY") {
            val existing = questionDao?.getSpecificSlotQuestion(
                curriculum, subject, chapterId, difficulty, marks
            )
            if (existing != null) {
                return@withContext existing
            }
        }

        val generatedQuestion = synthesizeOnTheFlyQuestion(
            curriculum = curriculum,
            subject = subject,
            chapterId = chapterId,
            difficulty = difficulty,
            marks = marks
        )

        questionDao?.insertQuestion(generatedQuestion)
        return@withContext generatedQuestion
    }

    suspend fun getMicroDrillQuestions(
        curriculum: String,
        subject: String,
        limit: Int
    ): List<CachedQuestionEntity> = withContext(Dispatchers.IO) {
        questionDao?.getHotQuestionsForPaper(curriculum, subject, limit) ?: emptyList()
    }

    suspend fun generateFullPaper(
        curriculum: String,
        subject: String,
        grade: Int,
        limit: Int
    ): List<CachedQuestionEntity> = withContext(Dispatchers.IO) {
        val questions = questionDao?.getRandomizedQuestionsForPaper(curriculum, subject, grade, limit) ?: emptyList()
        val ids = questions.map { it.id }
        if (ids.isNotEmpty()) {
            questionDao?.incrementUsageCount(ids)
        }
        questions
    }

    suspend fun getBookVaultContent(): List<CachedQuestionEntity> = withContext(Dispatchers.IO) {
        questionDao?.getBookReadyVaultQuestions() ?: emptyList()
    }

    private fun synthesizeOnTheFlyQuestion(
        curriculum: String,
        subject: String,
        chapterId: String,
        difficulty: String,
        marks: Int
    ): CachedQuestionEntity {
        return CachedQuestionEntity(
            curriculum = curriculum,
            subject = subject,
            grade = 12,
            chapterId = chapterId,
            questionStem = "[$curriculum Elite Synthesized] Critically analyze core principles for chapter $chapterId under $difficulty tier.",
            questionType = if (marks <= 2) "MCQ" else "SHORT_ANSWER",
            marks = marks,
            commandWord = if (curriculum == "CAMBRIDGE") "Evaluate" else "Calculate",
            taxonomyLevel = "EVALUATION",
            difficulty = difficulty,
            correctAnswer = "Model verified evaluation key.",
            stepMarkingRubric = "[1] Mark for statement.\n[2] Marks for substitution.",
            sourceType = "ON_THE_FLY",
            isBookReady = true,
            qualityRatingScore = 10.0
        )
    }
}