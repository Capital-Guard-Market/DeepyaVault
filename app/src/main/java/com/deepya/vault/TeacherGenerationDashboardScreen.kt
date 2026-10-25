package com.deepya.vault.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepya.vault.ui.SubjectSearchDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherGenerationDashboardScreen(
    onBackClick: () -> Unit,
    onProceedToSlotBuilder: (board: String, subject: String, source: String, extraConfig: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("School Boards") } // "School Boards" or "Competitive Tracks"
    var selectedCurriculum by remember { mutableStateOf("CBSE Grade 11-12") }
    var selectedSubject by remember { mutableStateOf("Accountancy (055)") }
    var selectedCompetitiveExam by remember { mutableStateOf("JEE Main") }
    var selectedCambridgePaper by remember { mutableStateOf("Paper 1 (MCQ)") }
    var selectedSource by remember { mutableStateOf("Question Bank") }
    var totalMarks by remember { mutableStateOf("50") }
    var showSubjectDialog by remember { mutableStateOf(false) }
    var showCompetitiveDropdown by remember { mutableStateOf(false) }

    val offlineSources = listOf("PYQ", "SQP", "Question Bank", "Random Mix")
    val cambridgePapers = listOf("Paper 1 (MCQ)", "Paper 2 (Structured)", "Paper 3 (Advanced)", "Paper 4 (Data/Practical)")
    val competitiveExamsList = mapOf(
        "JEE Main" to listOf("Physics", "Chemistry", "Mathematics"),
        "NEET" to listOf("Physics", "Chemistry", "Botany", "Zoology"),
        "IPMAT" to listOf("Quantitative Ability", "Verbal Ability"),
        "Cambridge A-Level" to listOf("Mathematics", "Biology", "Chemistry", "Physics", "Economics")
    )

    val isCambridge = selectedCurriculum.contains("Cambridge", ignoreCase = true)
    val isCompetitive = selectedCategory == "Competitive Tracks"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Command Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Elite Assessment Builder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Configure curriculum track, target subject, and specialized paper parameters.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Category Toggle (School Boards vs Competitive Tracks)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    text = "School Boards",
                    isSelected = selectedCategory == "School Boards",
                    modifier = Modifier.weight(1f)
                ) {
                    selectedCategory = "School Boards"
                    selectedCurriculum = "CBSE Grade 11-12"
                }
                TabButton(
                    text = "Competitive Tracks",
                    isSelected = selectedCategory == "Competitive Tracks",
                    modifier = Modifier.weight(1f)
                ) {
                    selectedCategory = "Competitive Tracks"
                    selectedCurriculum = "JEE Main"
                }
            }

            // Dynamic Sub-Selection (Boards List OR Clean Dropdown for Competitive)
            if (!isCompetitive) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select Board Curriculum", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("CBSE Grade 11-12", "TS State Board", "Cambridge A-Level").forEach { board ->
                            CurriculumCard(
                                title = board,
                                isSelected = selectedCurriculum == board,
                                modifier = Modifier.weight(1f)
                            ) {
                                selectedCurriculum = board
                                if (board.contains("Cambridge", ignoreCase = true)) {
                                    selectedSubject = "Economics (9708) / Accounting"
                                }
                            }
                        }
                    }
                }
            } else {
                // Clean Dropdown for Competitive Exams
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Select Competitive Examination", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.tertiary)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCompetitiveDropdown = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Active Examination Track", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    Text(selectedCompetitiveExam, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }

                        DropdownMenu(
                            expanded = showCompetitiveDropdown,
                            onDismissRequest = { showCompetitiveDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            competitiveExamsList.keys.forEach { examName ->
                                DropdownMenuItem(
                                    text = { Text(examName, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        selectedCompetitiveExam = examName
                                        selectedCurriculum = examName
                                        showCompetitiveDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Display Exam Subject Breakdown Matrix
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Included Subjects in $selectedCompetitiveExam Structure:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            val subjectsInExam = competitiveExamsList[selectedCompetitiveExam] ?: emptyList()
                            Text(
                                text = subjectsInExam.joinToString(" • "),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Cambridge 4-Paper Component Selection Row (Appears ONLY when Cambridge is chosen)
            if (isCambridge) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select Cambridge Paper Component", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cambridgePapers.forEach { paper ->
                            FilterChip(
                                selected = selectedCambridgePaper == paper,
                                onClick = { selectedCambridgePaper = paper },
                                label = { Text(paper.substringBefore(" "), fontSize = 10.sp, maxLines = 1) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Target Subject Selection Box (For Boards)
            if (!isCompetitive) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Target Subject ($selectedCurriculum)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSubjectDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Tap to browse database subjects", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = selectedSubject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Icon(Icons.Default.Edit, contentDescription = "Change Subject", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Total Marks & Source Engines Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = totalMarks,
                    onValueChange = { totalMarks = it },
                    label = { Text("Marks") },
                    modifier = Modifier.weight(0.3f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Column(modifier = Modifier.weight(0.7f)) {
                    Text("Vault Source Engine", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        offlineSources.take(2).forEach { src ->
                            FilterChip(
                                selected = selectedSource == src,
                                onClick = { selectedSource = src },
                                label = { Text(src, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        offlineSources.drop(2).forEach { src ->
                            FilterChip(
                                selected = selectedSource == src,
                                onClick = { selectedSource = src },
                                label = { Text(src, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Online AI Generation Engine Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedSource = "Online AI Generator" },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedSource == "Online AI Generator") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Online AI", tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(text = "Online AI Generation Engine", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Synthesize fresh dynamic questions via cloud AI", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    RadioButton(
                        selected = selectedSource == "Online AI Generator",
                        onClick = { selectedSource = "Online AI Generator" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    val configDetails = if (isCambridge) selectedCambridgePaper else if (isCompetitive) selectedCompetitiveExam else "Standard"
                    onProceedToSlotBuilder(selectedCurriculum, selectedSubject, selectedSource, configDetails)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Proceed to Itemized Slot Builder →", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    if (showSubjectDialog) {
        SubjectSearchDialog(
            onDismiss = { showSubjectDialog = false },
            onSubjectSelected = { sub ->
                selectedSubject = sub.displayName
                showSubjectDialog = false
            }
        )
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CurriculumCard(title: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}