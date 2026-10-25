package com.deepya.vault

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

data class GeneratedPaperItem(
    val title: String,
    val gradeSubject: String,
    val marks: String,
    val pdfFileName: String,
    val wordFileName: String
)

@Composable
fun ProfileDownloadsScreen() {
    val context = LocalContext.current

    val samplePapers = remember {
        listOf(
            GeneratedPaperItem(
                title = "Class Test - 1",
                gradeSubject = "NEET • Grade XII • Physics",
                marks = "15 Marks",
                pdfFileName = "Class_Test_1_Physics.pdf",
                wordFileName = "Class_Test_1_Physics.txt"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FD))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Profile & Downloads Hub",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Teacher Wallet Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Teacher Wallet Balance",
                    fontSize = 14.sp,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "195.0 Credits",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Generated Papers List Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📁", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generated Papers (${samplePapers.size})",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(samplePapers) { paper ->
                PaperCardItem(
                    paper = paper,
                    onOpenDocument = { isPdf ->
                        val fileName = if (isPdf) paper.pdfFileName else paper.wordFileName
                        val mimeType = if (isPdf) "application/pdf" else "text/plain"
                        openGeneratedFile(context, fileName, mimeType, paper.title, paper.gradeSubject)
                    }
                )
            }
        }
    }
}

@Composable
fun PaperCardItem(
    paper: GeneratedPaperItem,
    onOpenDocument: (isPdf: Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = paper.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = paper.marks,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = paper.gradeSubject,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onOpenDocument(true) },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Open PDF ↗", fontSize = 13.sp, color = Color(0xFF334155))
                }

                OutlinedButton(
                    onClick = { onOpenDocument(false) },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Open Text ↗", fontSize = 13.sp, color = Color(0xFF334155))
                }
            }
        }
    }
}

fun openGeneratedFile(context: Context, fileName: String, mimeType: String, title: String, subject: String) {
    try {
        val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        val file = File(targetDir, fileName)

        if (fileName.endsWith(".pdf")) {
            generateValidPdfFile(file, title, subject)
        } else {
            file.writeText("DEEPYA VAULT - QUESTION BANK\nTitle: $title\nSubject: $subject\nTotal Marks: 15\n\nQ1. State Coulomb's Law.\nQ2. Derive the electric field of an electric dipole.")
        }

        val authority = "${context.packageName}.fileprovider"
        val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Open with")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Error opening file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}

fun generateValidPdfFile(file: File, title: String, subject: String) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    paint.isFakeBoldText = true
    paint.textSize = 18f
    paint.color = AndroidColor.BLACK
    canvas.drawText("DEEPYA VAULT - QUESTION PAPER", 40f, 50f, paint)

    paint.textSize = 12f
    paint.isFakeBoldText = false
    paint.color = AndroidColor.DKGRAY
    canvas.drawText("Assessment: $title", 40f, 75f, paint)
    canvas.drawText("Subject Details: $subject", 40f, 95f, paint)
    canvas.drawText("Time Allowed: 45 Minutes | Maximum Marks: 15", 40f, 115f, paint)

    paint.color = AndroidColor.LTGRAY
    paint.strokeWidth = 1.5f
    canvas.drawLine(40f, 130f, 555f, 130f, paint)

    paint.color = AndroidColor.BLACK
    paint.isFakeBoldText = true
    paint.textSize = 13f
    canvas.drawText("SECTION A: MULTIPLE CHOICE QUESTIONS (1 Mark Each)", 40f, 160f, paint)

    paint.isFakeBoldText = false
    paint.textSize = 11f
    canvas.drawText("1. What is the SI unit of electric flux?", 40f, 185f, paint)
    canvas.drawText("   (A) N m² C⁻¹      (B) N m C⁻¹      (C) N m² C⁻²      (D) V m⁻¹", 40f, 205f, paint)

    canvas.drawText("2. The electrostatic force between two charges is inversely proportional to:", 40f, 235f, paint)
    canvas.drawText("   (A) Distance r    (B) Square of distance r²   (C) r³          (D) None", 40f, 255f, paint)

    paint.isFakeBoldText = true
    paint.textSize = 13f
    canvas.drawText("SECTION B: DESCRIPTIVE QUESTIONS", 40f, 295f, paint)

    paint.isFakeBoldText = false
    paint.textSize = 11f
    canvas.drawText("3. State Gauss's Law and express it in mathematical form. [3 Marks]", 40f, 320f, paint)
    canvas.drawText("4. Derive the expression for electric field on the axial line of an electric dipole. [5 Marks]", 40f, 350f, paint)
    canvas.drawText("5. Explain the principle and working of a parallel plate capacitor. [5 Marks]", 40f, 380f, paint)

    pdfDocument.finishPage(page)

    file.parentFile?.mkdirs()
    FileOutputStream(file).use { out ->
        pdfDocument.writeTo(out)
    }
    pdfDocument.close()
}