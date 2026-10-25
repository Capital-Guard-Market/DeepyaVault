package com.deepya.vault.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepya.vault.data.model.CurriculumTrack
import com.deepya.vault.data.model.QuestionBankChapter
import com.deepya.vault.data.repository.QuestionRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankListScreen(
    track: CurriculumTrack,
    context: Context,               // Required to pass context for asset loading
    repository: QuestionRepository, // Integrated with your actual QuestionRepository
    onBack: () -> Unit,
    onViewChapter: (QuestionBankChapter) -> Unit,
    onShareChapter: (QuestionBankChapter) -> Unit,
    onDownloadChapter: (QuestionBankChapter) -> Unit
) {
    // Extract title details based on track
    val subtitleText = when (track) {
        is CurriculumTrack.SchoolBoard -> "${track.boardName} • ${track.subject}" + (track.paperComponent?.let { " • $it" } ?: "")
        is CurriculumTrack.Competitive -> "${track.examName} • ${track.subject}"
    }

    // Load chapters dynamically via QuestionRepository and CurriculumLoader
    var chapters by remember {
        mutableStateOf(repository.getChaptersForTrack(track, context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Question Bank Vault", fontWeight = FontWeight.Bold)
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
                .padding(16.dp)
        ) {
            Text(
                text = "Chapter-Wise Practice Packs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "High-volume chapter question packs (80–300+ Qs). Lazy download or share instantly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chapters) { chapter ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chapter.chapterTitle,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                        Text("${chapter.totalQuestions} Questions", modifier = Modifier.padding(4.dp))
                                    }
                                    Badge(containerColor = MaterialTheme.colorScheme.surface) {
                                        Text("${chapter.fileSizeKb} KB", modifier = Modifier.padding(4.dp))
                                    }
                                }
                            }

                            // Dynamic state action buttons
                            if (chapter.isDownloaded) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = { onViewChapter(chapter) }) {
                                        Icon(Icons.Default.Visibility, contentDescription = "View PDF", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { onShareChapter(chapter) }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share with Students", tint = MaterialTheme.colorScheme.tertiary)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        // Simulate lazy download toggle for preview
                                        chapters = chapters.map {
                                            if (it.chapterId == chapter.chapterId) it.copy(isDownloaded = true, localFilePath = "downloaded_path.pdf")
                                            else it
                                        }
                                        onDownloadChapter(chapter)
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}