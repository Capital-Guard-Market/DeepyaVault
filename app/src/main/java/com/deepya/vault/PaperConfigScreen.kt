package com.deepya.vault

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepya.vault.data.model.CurriculumTrack
import com.deepya.vault.data.model.QuestionBankChapter
import com.deepya.vault.data.repository.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DetailedChapterConfig(
    val chapter: QuestionBankChapter,
    val severity: String,
    val mcqCount: Int,
    val arCount: Int,
    val vsaCount: Int,
    val saCount: Int,
    val laCount: Int
)

data class SectionMarkScheme(
    val mcqMarks: Int = 1,
    val arMarks: Int = 1,
    val vsaMarks: Int = 2,
    val saMarks: Int = 3,
    val laMarks: Int = 6
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperConfigScreen(
    track: CurriculumTrack,
    repository: QuestionRepository,
    onGenerateClicked: (
        syllabusEdition: String,
        totalMarks: Int,
        totalQuestions: Int,
        sourceEngine: String,
        markScheme: SectionMarkScheme,
        configuredChapters: List<DetailedChapterConfig>
    ) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedSyllabus by remember { mutableStateOf("2025-26 Rationalized") }
    var selectedSourceEngine by remember { mutableStateOf("PYQ & SQP") }

    // Section-wise Marks Customizer (Requirement 4)
    var mcqMarksText by remember { mutableStateOf("1") }
    var arMarksText by remember { mutableStateOf("1") }
    var vsaMarksText by remember { mutableStateOf("2") }
    var saMarksText by remember { mutableStateOf("3") }
    var laMarksText by remember { mutableStateOf("6") }

    var chapters by remember { mutableStateOf(emptyList<QuestionBankChapter>()) }
    var selectedChapterIds by remember { mutableStateOf(setOf<String>()) }
    var chapterSeverities by remember { mutableStateOf(mapOf<String, String>()) }

    // Per-Chapter Question Counts (Requirements 1 & 5)
    var chapterMcqCounts by remember { mutableStateOf(mapOf<String, Int>()) }
    var chapterArCounts by remember { mutableStateOf(mapOf<String, Int>()) }
    var chapterVsaCounts by remember { mutableStateOf(mapOf<String, Int>()) }
    var chapterSaCounts by remember { mutableStateOf(mapOf<String, Int>()) }
    var chapterLaCounts by remember { mutableStateOf(mapOf<String, Int>()) }

    val severityOptions = listOf("Easy", "Moderate", "Hard", "Elite")
    val sourceEngines = listOf(
        "PYQ & SQP" to "Previous Year Questions & Official Specimen Papers",
        "Question Bank" to "Curated chapter-wise practice repository",
        "Random Mix (SQP + PYQ + Question Bank)" to "Balanced cross-engine selection across all sources",
        "Online AI Creator (On-The-Fly)" to "Synthesize fresh dynamic questions in real-time"
    )

    LaunchedEffect(track) {
        val loadedChapters = withContext(Dispatchers.IO) {
            repository.getChaptersForTrack(track, context)
        }
        chapters = loadedChapters
        selectedChapterIds = loadedChapters.map { it.chapterId }.toSet()
        chapterSeverities = loadedChapters.associate { it.chapterId to "Moderate" }
        chapterMcqCounts = loadedChapters.associate { it.chapterId to 2 }
        chapterArCounts = loadedChapters.associate { it.chapterId to 1 }
        chapterVsaCounts = loadedChapters.associate { it.chapterId to 1 }
        chapterSaCounts = loadedChapters.associate { it.chapterId to 1 }
        chapterLaCounts = loadedChapters.associate { it.chapterId to 0 }
    }

    // Parsed Section Marks
    val mcqM = mcqMarksText.toIntOrNull() ?: 1
    val arM = arMarksText.toIntOrNull() ?: 1
    val vsaM = vsaMarksText.toIntOrNull() ?: 2
    val saM = saMarksText.toIntOrNull() ?: 3
    val laM = laMarksText.toIntOrNull() ?: 6

    // Calculated Totals (Requirements 2 & 3)
    val selectedChapters = chapters.filter { selectedChapterIds.contains(it.chapterId) }
    val totalMcqCount = selectedChapters.sumOf { chapterMcqCounts[it.chapterId] ?: 0 }
    val totalArCount = selectedChapters.sumOf { chapterArCounts[it.chapterId] ?: 0 }
    val totalVsaCount = selectedChapters.sumOf { chapterVsaCounts[it.chapterId] ?: 0 }
    val totalSaCount = selectedChapters.sumOf { chapterSaCounts[it.chapterId] ?: 0 }
    val totalLaCount = selectedChapters.sumOf { chapterLaCounts[it.chapterId] ?: 0 }

    val grandTotalQuestions = totalMcqCount + totalArCount + totalVsaCount + totalSaCount + totalLaCount
    val grandTotalMarks = (totalMcqCount * mcqM) + (totalArCount * arM) + (totalVsaCount * vsaM) + (totalSaCount * saM) + (totalLaCount * laM)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paper Configuration", fontWeight = FontWeight.Bold) },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Generation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = "Elite Assessment Builder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Configure per-chapter question models, custom section marks, and auto-computed totals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // 1. Syllabus Edition Section
            Text(text = "Syllabus Edition", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("2025-26 Rationalized", "2023-24 Legacy").forEach { syllabus ->
                    FilterChip(
                        selected = selectedSyllabus == syllabus,
                        onClick = { selectedSyllabus = syllabus },
                        label = { Text(syllabus) },
                        leadingIcon = if (selectedSyllabus == syllabus) {
                            { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // 2. Section-Wise Marks Customizer (Requirement 4)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Custom Section Mark Scheme (Marks per Question Type)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Customize marks for each question type to match your subject blueprint:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MarkInputField(label = "MCQ", value = mcqMarksText, onValueChange = { mcqMarksText = it }, modifier = Modifier.weight(1f))
                        MarkInputField(label = "A&R", value = arMarksText, onValueChange = { arMarksText = it }, modifier = Modifier.weight(1f))
                        MarkInputField(label = "Very Short", value = vsaMarksText, onValueChange = { vsaMarksText = it }, modifier = Modifier.weight(1f))
                        MarkInputField(label = "Short", value = saMarksText, onValueChange = { saMarksText = it }, modifier = Modifier.weight(1f))
                        MarkInputField(label = "Long", value = laMarksText, onValueChange = { laMarksText = it }, modifier = Modifier.weight(1f))
                    }
                }
            }

            // 3. Per-Chapter Question Allocation & Severity Section (Requirements 1 & 5)
            Text(
                text = "Select Chapters & Configure Itemized Questions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (chapters.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedChapterIds.size} of ${chapters.size} selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    TextButton(onClick = {
                        if (selectedChapterIds.size == chapters.size) {
                            selectedChapterIds = emptySet()
                        } else {
                            selectedChapterIds = chapters.map { it.chapterId }.toSet()
                        }
                    }) {
                        Text(if (selectedChapterIds.size == chapters.size) "Deselect All" else "Select All")
                    }
                }

                chapters.forEach { chapter ->
                    val isSelected = selectedChapterIds.contains(chapter.chapterId)
                    val currentSeverity = chapterSeverities[chapter.chapterId] ?: "Moderate"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedChapterIds = if (isSelected) {
                                            selectedChapterIds - chapter.chapterId
                                        } else {
                                            selectedChapterIds + chapter.chapterId
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = chapter.chapterTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedChapterIds = if (checked) {
                                            selectedChapterIds + chapter.chapterId
                                        } else {
                                            selectedChapterIds - chapter.chapterId
                                        }
                                    }
                                )
                            }

                            // Per-Chapter Settings (visible when chapter is selected)
                            if (isSelected) {
                                // Severity selector
                                Text(
                                    text = "Chapter Severity:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    severityOptions.forEach { level ->
                                        val isLevelSelected = currentSeverity == level
                                        FilterChip(
                                            selected = isLevelSelected,
                                            onClick = {
                                                chapterSeverities = chapterSeverities.toMutableMap().apply {
                                                    put(chapter.chapterId, level)
                                                }
                                            },
                                            label = { Text(level, fontSize = 10.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // Chapter Question Allocation Counters (Requirements 1 & 5)
                                Text(
                                    text = "Allocated Question Models for this Chapter:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    QuestionTypeCounter(
                                        label = "MCQ ($mcqM m)",
                                        count = chapterMcqCounts[chapter.chapterId] ?: 0,
                                        onCountChange = { newCount ->
                                            chapterMcqCounts = chapterMcqCounts.toMutableMap().apply { put(chapter.chapterId, newCount) }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    QuestionTypeCounter(
                                        label = "A&R ($arM m)",
                                        count = chapterArCounts[chapter.chapterId] ?: 0,
                                        onCountChange = { newCount ->
                                            chapterArCounts = chapterArCounts.toMutableMap().apply { put(chapter.chapterId, newCount) }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    QuestionTypeCounter(
                                        label = "VSA ($vsaM m)",
                                        count = chapterVsaCounts[chapter.chapterId] ?: 0,
                                        onCountChange = { newCount ->
                                            chapterVsaCounts = chapterVsaCounts.toMutableMap().apply { put(chapter.chapterId, newCount) }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    QuestionTypeCounter(
                                        label = "SA ($saM m)",
                                        count = chapterSaCounts[chapter.chapterId] ?: 0,
                                        onCountChange = { newCount ->
                                            chapterSaCounts = chapterSaCounts.toMutableMap().apply { put(chapter.chapterId, newCount) }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    QuestionTypeCounter(
                                        label = "LA ($laM m)",
                                        count = chapterLaCounts[chapter.chapterId] ?: 0,
                                        onCountChange = { newCount ->
                                            chapterLaCounts = chapterLaCounts.toMutableMap().apply { put(chapter.chapterId, newCount) }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // 4. Automatic Section Breakdown & Grand Totals Display (Requirements 2 & 3)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Live Exam Blueprint Summary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = "Cross-check overall question allocation and total marks section-wise:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    // Breakdown Rows (Requirement 3)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SummaryRow("MCQs ($mcqM mark each)", totalMcqCount, totalMcqCount * mcqM)
                        SummaryRow("Assertion & Reason ($arM mark each)", totalArCount, totalArCount * arM)
                        SummaryRow("Very Short Answer ($vsaM marks each)", totalVsaCount, totalVsaCount * vsaM)
                        SummaryRow("Short Answer ($saM marks each)", totalSaCount, totalSaCount * saM)
                        SummaryRow("Long Answer ($laM marks each)", totalLaCount, totalLaCount * laM)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                    // Automatic Totals Display (Requirement 2)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Questions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text(text = "$grandTotalQuestions Qs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Total Exam Marks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text(text = "$grandTotalMarks Marks", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // 5. From where we should take questions from (Source Engines)
            Text(text = "Question Source Engine", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            sourceEngines.forEach { (engineTitle, engineDesc) ->
                val isEngineSelected = selectedSourceEngine == engineTitle
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSourceEngine = engineTitle },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEngineSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isEngineSelected,
                            onClick = { selectedSourceEngine = engineTitle }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = engineTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isEngineSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = engineDesc,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Proceed Action Button
            Button(
                onClick = {
                    val markScheme = SectionMarkScheme(mcqM, arM, vsaM, saM, laM)
                    val configuredList = selectedChapters.map { ch ->
                        DetailedChapterConfig(
                            chapter = ch,
                            severity = chapterSeverities[ch.chapterId] ?: "Moderate",
                            mcqCount = chapterMcqCounts[ch.chapterId] ?: 0,
                            arCount = chapterArCounts[ch.chapterId] ?: 0,
                            vsaCount = chapterVsaCounts[ch.chapterId] ?: 0,
                            saCount = chapterSaCounts[ch.chapterId] ?: 0,
                            laCount = chapterLaCounts[ch.chapterId] ?: 0
                        )
                    }

                    onGenerateClicked(
                        selectedSyllabus,
                        grandTotalMarks,
                        grandTotalQuestions,
                        selectedSourceEngine,
                        markScheme,
                        configuredList
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = selectedChapterIds.isNotEmpty() && grandTotalQuestions > 0
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = "Proceed to Paper Generation ($grandTotalQuestions Qs, $grandTotalMarks M) →", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MarkInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { char -> char.isDigit() }) },
        label = { Text(label, fontSize = 9.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier,
        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun QuestionTypeCounter(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { if (count > 0) onCountChange(count - 1) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Text(
                    text = count.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { onCountChange(count + 1) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, count: Int, marks: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(
            text = "$count Qs  •  $marks Marks",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}