package com.deepya.vault.data.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Data class representing a paper generation request
data class GeneratePaperRequest(
    val schoolName: String = "",
    val testName: String = "Class Test",
    val grade: String = "Grade XII",
    val subject: String = "Accountancy (055)",
    val board: String = "CBSE",
    val timeAllowed: String = "45 Minutes",
    val totalMarks: Int = 15
)

// Subject model for selection dialogs
data class SubjectItem(
    val code: String,
    val name: String,
    val category: String
) {
    val displayName: String
        get() = "$name ($code)"
}

// Global list of supported subjects across Commerce, Science, and Humanities
val ALL_SUBJECTS_LIST = listOf(
    SubjectItem("055", "Accountancy", "Commerce"),
    SubjectItem("065", "Informatics Practices", "Commerce / Science"),
    SubjectItem("054", "Business Studies", "Commerce"),
    SubjectItem("030", "Economics", "Commerce / Humanities"),
    SubjectItem("041", "Mathematics", "Science / Commerce"),
    SubjectItem("042", "Physics", "Science"),
    SubjectItem("043", "Chemistry", "Science"),
    SubjectItem("044", "Biology", "Science"),
    SubjectItem("083", "Computer Science", "Science"),
    SubjectItem("301", "English Core", "Language"),
    SubjectItem("027", "History", "Humanities"),
    SubjectItem("028", "Political Science", "Humanities"),
    SubjectItem("029", "Geography", "Humanities"),
    SubjectItem("037", "Psychology", "Humanities"),
    SubjectItem("039", "Sociology", "Humanities"),
    SubjectItem("048", "Physical Education", "General")
)

// Subject search and selection dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSearchDialog(
    onDismiss: () -> Unit,
    onSubjectSelected: (SubjectItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSubjects = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ALL_SUBJECTS_LIST
        } else {
            ALL_SUBJECTS_LIST.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Subject",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by subject name or code...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredSubjects) { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSubjectSelected(subject) },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = subject.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Code: ${subject.code} • ${subject.category}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text(
                                    text = "Select →",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}