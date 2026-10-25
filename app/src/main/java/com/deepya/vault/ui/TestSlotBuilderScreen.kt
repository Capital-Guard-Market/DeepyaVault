package com.deepya.vault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepya.vault.data.model.CurriculumTrack
import com.deepya.vault.engine.TestBlueprint
import com.deepya.vault.engine.TestSectionBlueprint

data class TestSlot(
    val slotId: String,
    val sectionName: String,
    val questionType: String,
    val marksPerQuestion: Int,
    val count: Int,
    val difficulty: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSlotBuilderScreen(
    track: CurriculumTrack,
    onBack: () -> Unit,
    onGeneratePaper: (List<TestSlot>) -> Unit
) {
    val subtitleText = when (track) {
        is CurriculumTrack.SchoolBoard -> "${track.boardName} • ${track.subject}"
        is CurriculumTrack.Competitive -> "${track.examName} • ${track.subject}"
    }

    var slots by remember {
        mutableStateOf(
            mutableListOf(
                TestSlot("1", "Section A - Multiple Choice", "MCQ", 1, 20, "Medium"),
                TestSlot("2", "Section B - Short Answer", "SHORT_ANSWER", 3, 6, "Hard"),
                TestSlot("3", "Section C - Analytical & Case Study", "CASE_STUDY", 4, 4, "HOTS"),
                TestSlot("4", "Section D - Comprehensive Problems", "LONG_ANSWER", 6, 3, "HOTS")
            )
        )
    }

    val totalMarks = slots.sumOf { it.marksPerQuestion * it.count }
    val totalQuestions = slots.sumOf { it.count }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Elite Test Blueprint & Slot Builder", fontWeight = FontWeight.Bold)
                        Text(text = subtitleText, style = MaterialTheme.typography.bodySmall)
                    }
                },
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
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Total Qs: $totalQuestions", fontWeight = FontWeight.Bold)
                        Text(text = "Total Marks: $totalMarks", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        onClick = {
                            // Map UI slots directly into the TestBlueprint required by the TestCompiler
                            val sectionBlueprints = slots.map { slot ->
                                TestSectionBlueprint(
                                    sectionTitle = slot.sectionName,
                                    chapterId = "ch_default_01",
                                    requiredCount = slot.count,
                                    allowedTypes = listOf(slot.questionType),
                                    allowedTaxonomyLevels = emptyList()
                                )
                            }

                            val blueprint = TestBlueprint(
                                paperTitle = when (track) {
                                    is CurriculumTrack.SchoolBoard -> "${track.boardName} ${track.subject} Assessment"
                                    is CurriculumTrack.Competitive -> "${track.examName} Mock Paper"
                                },
                                curriculumTrack = when (track) {
                                    is CurriculumTrack.SchoolBoard -> track.boardName
                                    is CurriculumTrack.Competitive -> track.examName
                                },
                                subject = when (track) {
                                    is CurriculumTrack.SchoolBoard -> track.subject
                                    is CurriculumTrack.Competitive -> track.subject
                                },
                                sections = sectionBlueprints
                            )

                            onGeneratePaper(slots)
                        },
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compile & Generate Paper")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configure Section Slots",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(
                    onClick = {
                        val newId = (slots.size + 1).toString()
                        slots = slots.toMutableList().apply {
                            add(TestSlot(newId, "Section Custom", "SHORT_ANSWER", 2, 5, "Medium"))
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Slot")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(slots) { slot ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = slot.sectionName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                IconButton(
                                    onClick = {
                                        slots = slots.toMutableList().apply { remove(slot) }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Slot", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                    Text("Type: ${slot.questionType}", modifier = Modifier.padding(4.dp))
                                }
                                Badge(containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
                                    Text("${slot.count} Questions", modifier = Modifier.padding(4.dp))
                                }
                                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                    Text("${slot.marksPerQuestion} Mark(s) Each", modifier = Modifier.padding(4.dp))
                                }
                                Badge {
                                    Text("Tier: ${slot.difficulty}", modifier = Modifier.padding(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}