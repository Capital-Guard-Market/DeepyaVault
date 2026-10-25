package com.deepya.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepya.vault.data.model.QuestionSlotConfig
import com.deepya.vault.ui.viewmodel.TeacherUiState
import com.deepya.vault.ui.viewmodel.TeacherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCommandCenterScreen(
    viewModel: TeacherViewModel,
    curriculum: String = "CBSE Grade 11-12",
    subject: String = "Accountancy (055)",
    onBackClick: () -> Unit,
    onPaperGenerated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var slotConfigs by remember {
        mutableStateOf(
            mutableListOf(
                QuestionSlotConfig(1, "CH_01", "MEDIUM", 1, "MCQ", "QUESTION_BANK", "PAPER_1_MCQ"),
                QuestionSlotConfig(2, "CH_02", "HARD", 3, "SHORT_ANSWER", "ON_THE_FLY", "PAPER_2_STRUCTURED"),
                QuestionSlotConfig(3, "CH_03", "ELITE", 6, "NUMERICAL", "QUESTION_BANK", "PAPER_3_ANSWER")
            )
        )
    }

    val isCambridge = curriculum.contains("Cambridge", ignoreCase = true)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Teacher Command Center", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val nextNum = slotConfigs.size + 1
                    slotConfigs = (slotConfigs + QuestionSlotConfig(
                        slotNumber = nextNum,
                        chapterId = "CH_$nextNum",
                        difficulty = "MEDIUM",
                        marks = if (isCambridge) 2 else 3,
                        questionMode = if (isCambridge) "STRUCTURED" else "SHORT_ANSWER",
                        sourceType = "QUESTION_BANK",
                        cambridgePaperType = if (isCambridge) "PAPER_2_STRUCTURED" else "STANDARD"
                    )).toMutableList()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Question Slot")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Surgical Itemized Slot Builder",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Configuring parameters for $curriculum ($subject).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(slotConfigs) { index, slot ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Question Slot #${slot.slotNumber}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 13.sp
                                )
                                IconButton(
                                    onClick = {
                                        if (slotConfigs.size > 1) {
                                            slotConfigs = slotConfigs.toMutableList().apply { removeAt(index) }
                                        } else {
                                            scope.launch { snackbarHostState.showSnackbar("Paper must contain at least one question slot.") }
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Slot", tint = Color.Red)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (isCambridge) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = "Component: ${slot.cambridgePaperType.replace("_", " ")}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Difficulty: ${slot.difficulty}", fontSize = 11.sp)
                                Text("Marks: ${slot.marks}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Mode: ${slot.questionMode}", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Source Engine: ${slot.sourceType}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    viewModel.generateItemizedPaper(curriculum, subject, slotConfigs.toList())
                    onPaperGenerated()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Generate Surgical Paper & Vault Elite Items", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (uiState is TeacherUiState.Loading) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}