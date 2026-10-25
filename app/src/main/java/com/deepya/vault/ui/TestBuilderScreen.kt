package com.deepya.vault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepya.vault.data.model.CurriculumTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestBuilderScreen(
    track: CurriculumTrack,
    onBack: () -> Unit,
    onProceedToSlotBuilder: (selectedSource: String) -> Unit
) {
    val subtitleText = when (track) {
        is CurriculumTrack.SchoolBoard -> "${track.boardName} • ${track.subject}" + (track.paperComponent?.let { " • $it" } ?: "")
        is CurriculumTrack.Competitive -> "${track.examName} • ${track.subject}"
    }

    // Available source engines based on track type
    val sourceOptions = if (track is CurriculumTrack.SchoolBoard) {
        listOf(
            "Previous Year Questions (PYQs)" to "Authentic board examination questions from past years.",
            "Sample Question Papers (SQPs)" to "Official board-released specimen and practice papers.",
            "Curated Chapter Question Bank" to "High-volume chapter-wise practice sets (80–120+ questions).",
            "Online AI Generator (On-The-Fly)" to "Synthesize fresh, unique questions instantly using cloud AI."
        )
    } else {
        listOf(
            "Past Entrance Test Papers" to "Real exam memory-based or official past papers.",
            "High-Yield Competitive Vault" to "Intensive multi-tier practice sets (150–300+ questions).",
            "Online AI Generator (On-The-Fly)" to "Generate adaptive competitive items with custom difficulty tiers."
        )
    }

    var selectedSource by remember { mutableStateOf(sourceOptions.first().first) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Elite Assessment Builder", fontWeight = FontWeight.Bold)
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Question Source Engine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose the repository engine that will power your mark blueprints and slot distribution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Source Options Cards / Radio Group
                sourceOptions.forEach { (title, description) ->
                    val isSelected = selectedSource == title
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        onClick = { selectedSource = title }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedSource = title }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Action Button to proceed to Slot Builder
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = { onProceedToSlotBuilder(selectedSource) }
            ) {
                Text("Proceed to Itemized Slot Builder →", fontWeight = FontWeight.Bold)
            }
        }
    }
}