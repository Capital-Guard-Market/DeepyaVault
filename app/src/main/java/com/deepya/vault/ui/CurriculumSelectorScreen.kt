package com.deepya.vault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepya.vault.data.model.AppGatewayMode
import com.deepya.vault.data.model.CurriculumTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumSelectorScreen(
    gatewayMode: AppGatewayMode,
    onTrackSelected: (CurriculumTrack) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = School Boards, 1 = Competitive

    // Grade state (11 = Class XI, 12 = Class XII)
    var selectedGrade by remember { mutableIntStateOf(12) }

    // School Board state
    var selectedBoard by remember { mutableStateOf("CBSE") }
    var selectedSchoolSubject by remember { mutableStateOf("Accountancy (055)") }
    var selectedCambridgeComponent by remember { mutableStateOf("Paper 1: MCQ") }

    // Competitive state
    var selectedExam by remember { mutableStateOf("CA Foundation") }
    var selectedCompetitiveSubject by remember { mutableStateOf("Accounting") }

    val titleText = if (gatewayMode == AppGatewayMode.QUESTION_BANKS) "Question Bank Vault" else "Test Builder"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$titleText - Select Track") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Top Tab Switcher
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("School Boards", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Competitive Tracks", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grade Selection Section (Grade 11 vs Grade 12)
            Text("Select Academic Grade", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(11 to "Grade XI (Class 11)", 12 to "Grade XII (Class 12)").forEach { (grade, label) ->
                    FilterChip(
                        selected = selectedGrade == grade,
                        onClick = { selectedGrade = grade },
                        label = { Text(label, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // SCHOOL BOARDS TAB CONTENT
                Text("Select Board", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CBSE", "ISC", "Cambridge").forEach { board ->
                        FilterChip(
                            selected = selectedBoard == board,
                            onClick = {
                                selectedBoard = board
                                selectedSchoolSubject = getSubjectsForBoard(board).first()
                            },
                            label = { Text(board) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Select Subject (Grade $selectedGrade)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))

                val schoolSubjects = getSubjectsForBoard(selectedBoard)

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(schoolSubjects) { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedSchoolSubject == subject)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = { selectedSchoolSubject = subject }
                        ) {
                            Text(
                                text = subject,
                                modifier = Modifier.padding(14.dp),
                                fontWeight = if (selectedSchoolSubject == subject) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Conditional Cambridge Component Picker row
                    if (selectedBoard == "Cambridge") {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Select Paper Component", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Paper 1: MCQ", "Paper 2: Structured", "Paper 3: Advanced", "Paper 4: Essay").forEach { comp ->
                                    FilterChip(
                                        selected = selectedCambridgeComponent == comp,
                                        onClick = { selectedCambridgeComponent = comp },
                                        label = { Text(comp.substringBefore(":")) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onTrackSelected(
                            CurriculumTrack.SchoolBoard(
                                boardName = selectedBoard,
                                subject = selectedSchoolSubject,
                                grade = selectedGrade,
                                paperComponent = if (selectedBoard == "Cambridge") selectedCambridgeComponent else null
                            )
                        )
                    }
                ) {
                    val gradeText = if (selectedGrade == 11) "Class XI" else "Class XII"
                    Text("Proceed with $selectedBoard $gradeText ($selectedSchoolSubject)", fontWeight = FontWeight.Bold)
                }

            } else {
                // COMPETITIVE TRACKS TAB CONTENT
                Text("Select Target Exam", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))

                val exams = listOf("CA Foundation", "CUET", "JEE Main", "NEET (UG)", "BITSAT", "IPMAT", "CLAT")

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(exams) { exam ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedExam == exam)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = {
                                selectedExam = exam
                                selectedCompetitiveSubject = getSubjectsForExam(exam).first()
                            }
                        ) {
                            Text(
                                text = exam,
                                modifier = Modifier.padding(14.dp),
                                fontWeight = if (selectedExam == exam) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Select Subject for $selectedExam", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(6.dp))

                        val compSubjects = getSubjectsForExam(selectedExam)
                        compSubjects.forEach { sub ->
                            ElevatedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = { selectedCompetitiveSubject = sub },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = if (selectedCompetitiveSubject == sub)
                                        MaterialTheme.colorScheme.secondaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Text(sub)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onTrackSelected(
                            CurriculumTrack.Competitive(
                                examName = selectedExam,
                                subject = selectedCompetitiveSubject,
                                grade = selectedGrade
                            )
                        )
                    }
                ) {
                    Text("Proceed with $selectedExam ($selectedCompetitiveSubject)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Comprehensive helper for School Board Subjects including Commerce, Science & Humanities
fun getSubjectsForBoard(board: String): List<String> {
    return when (board) {
        "CBSE" -> listOf(
            // Commerce & Management
            "Accountancy (055)",
            "Business Studies (054)",
            "Economics (030)",
            "Entrepreneurship (066)",
            // Mathematics & Computing
            "Mathematics (041)",
            "Applied Mathematics (241)",
            "Computer Science (083)",
            "Informatics Practices (065)",
            // Sciences
            "Physics (042)",
            "Chemistry (043)",
            "Biology (044)",
            // Humanities & Languages
            "History (027)",
            "Political Science (028)",
            "Geography (029)",
            "Sociology (039)",
            "Psychology (037)",
            "English Core (301)"
        )
        "ISC" -> listOf(
            // Commerce & Management
            "Accounts",
            "Commerce",
            "Economics",
            "Business Studies",
            // Mathematics & Computing
            "Mathematics",
            "Computer Science",
            // Sciences
            "Physics",
            "Chemistry",
            "Biology",
            // Humanities & Languages
            "History",
            "Political Science",
            "Geography",
            "Sociology",
            "Psychology",
            "English Literature",
            "English Language"
        )
        "Cambridge" -> listOf(
            "Accounting (9706)",
            "Economics (9708)",
            "Business (9609)",
            "Mathematics (9709)",
            "Biology (9700)",
            "Chemistry (9701)",
            "Physics (9702)",
            "Sociology (9699)",
            "Psychology (9990)",
            "History (9489)",
            "Global Perspectives & Research (9239)"
        )
        else -> listOf("Accountancy (055)")
    }
}

// Comprehensive helper for Competitive Exam Subjects
fun getSubjectsForExam(exam: String): List<String> {
    return when (exam) {
        "CA Foundation" -> listOf("Accounting", "Business Laws", "Quantitative Aptitude", "Business Economics")
        "CUET" -> listOf("Accountancy", "Business Studies", "Economics", "Mathematics", "Physics", "Chemistry", "Biology", "History", "Political Science", "Geography", "Sociology", "Psychology", "General Test", "English")
        "JEE Main" -> listOf("Physics", "Chemistry", "Mathematics")
        "NEET (UG)" -> listOf("Physics", "Chemistry", "Botany", "Zoology")
        "BITSAT" -> listOf("Physics", "Chemistry", "Mathematics", "English & Logical Reasoning")
        "IPMAT" -> listOf("Quantitative Ability", "Verbal Ability & Logical Reasoning")
        "CLAT" -> listOf("English Language", "Current Affairs & GK", "Legal Reasoning", "Logical Reasoning", "Quantitative Techniques")
        else -> listOf("General")
    }
}