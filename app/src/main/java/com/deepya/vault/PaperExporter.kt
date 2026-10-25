package com.deepya.vault

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object PaperExporter {

    // 1. Export as Native Printable PDF
    fun exportToPdf(context: Context, paper: GeneratedPaper, includeScheme: Boolean) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 points
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 16f
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        }

        val textPaint = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 10f
            color = Color.BLACK
        }

        val boldPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 10f
            color = Color.BLACK
        }

        var yPosition = 50f
        val margin = 40f
        val pageWidth = 595f

        // Draw Header
        canvas.drawText(paper.blueprint.schoolOrInstitute.uppercase(), pageWidth / 2, yPosition, titlePaint)
        yPosition += 20f
        titlePaint.textSize = 12f
        canvas.drawText(paper.blueprint.examTitle.uppercase(), pageWidth / 2, yPosition, titlePaint)
        yPosition += 20f

        canvas.drawText("Subject: ${paper.blueprint.subjectCode}", margin, yPosition, boldPaint)
        canvas.drawText("Max Marks: ${paper.blueprint.totalMarks}", pageWidth - margin - 80f, yPosition, boldPaint)
        yPosition += 15f
        canvas.drawText("Time Allowed: ${paper.blueprint.durationMinutes} Minutes", margin, yPosition, boldPaint)
        canvas.drawText("Class: XII", pageWidth - margin - 80f, yPosition, boldPaint)
        yPosition += 15f

        canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, boldPaint)
        yPosition += 20f

        // Draw Questions
        paper.sections.forEach { (section, questions) ->
            if (yPosition > 780f) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                yPosition = 50f
            }

            canvas.drawText(section.uppercase(), margin, yPosition, boldPaint)
            yPosition += 15f

            questions.forEach { q ->
                if (yPosition > 760f) {
                    document.finishPage(page)
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = 50f
                }

                canvas.drawText("Q${q.qNumber}. ${q.questionText.take(85)}", margin, yPosition, textPaint)
                canvas.drawText("[${q.marks}]", pageWidth - margin - 20f, yPosition, boldPaint)
                yPosition += 14f

                if (q.questionText.length > 85) {
                    canvas.drawText(q.questionText.substring(85).take(90), margin + 18f, yPosition, textPaint)
                    yPosition += 14f
                }

                q.options.forEach { opt ->
                    canvas.drawText(opt, margin + 15f, yPosition, textPaint)
                    yPosition += 12f
                }

                if (includeScheme && q.markingScheme.isNotBlank()) {
                    yPosition += 4f
                    canvas.drawText("Solution: ${q.markingScheme.take(80)}", margin + 15f, yPosition, boldPaint)
                    yPosition += 14f
                }

                yPosition += 10f
            }
            yPosition += 10f
        }

        document.finishPage(page)

        try {
            val file = File(context.cacheDir, "${paper.blueprint.examTitle.replace(" ", "_")}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            shareFile(context, file, "application/pdf")
        } catch (e: Exception) {
            Toast.makeText(context, "PDF Generation Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // 2. Export as Editable Microsoft Word (.docx) Document
    fun exportToWord(context: Context, paper: GeneratedPaper, includeScheme: Boolean) {
        val fileName = "${paper.blueprint.examTitle.replace(" ", "_")}.docx"
        val file = File(context.cacheDir, fileName)

        try {
            val bodyBuilder = StringBuilder()

            // Header Section
            bodyBuilder.append("<w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val=\"36\"/></w:rPr><w:t>${paper.blueprint.schoolOrInstitute.uppercase()}</w:t></w:r></w:p>")
            bodyBuilder.append("<w:p><w:pPr><w:jc w:val=\"center\"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val=\"28\"/></w:rPr><w:t>${paper.blueprint.examTitle.uppercase()}</w:t></w:r></w:p>")
            bodyBuilder.append("<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Subject: ${paper.blueprint.subjectCode} | Max Marks: ${paper.blueprint.totalMarks} | Time: ${paper.blueprint.durationMinutes} Mins</w:t></w:r></w:p>")
            bodyBuilder.append("<w:p><w:r><w:t>--------------------------------------------------------------------------------------------------------</w:t></w:r></w:p>")

            // Content Section
            paper.sections.forEach { (section, questions) ->
                bodyBuilder.append("<w:p><w:r><w:rPr><w:b/><w:sz w:val=\"26\"/></w:rPr><w:t>${section.uppercase()}</w:t></w:r></w:p>")

                questions.forEach { q ->
                    bodyBuilder.append("<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Q${q.qNumber}. </w:t></w:r><w:r><w:t>${escapeXml(q.questionText)} [${q.marks} Mark${if (q.marks > 1) "s" else ""}]</w:t></w:r></w:p>")

                    q.options.forEach { opt ->
                        bodyBuilder.append("<w:p><w:r><w:t>     ${escapeXml(opt)}</w:t></w:r></w:p>")
                    }

                    if (includeScheme && q.markingScheme.isNotBlank()) {
                        bodyBuilder.append("<w:p><w:r><w:rPr><w:i/><w:color w:val=\"555555\"/></w:rPr><w:t>     [Key]: ${escapeXml(q.markingScheme)}</w:t></w:r></w:p>")
                    }
                    bodyBuilder.append("<w:p/>")
                }
            }

            // Write ZIP OpenXML Structure
            ZipOutputStream(FileOutputStream(file)).use { zos ->
                // [Content_Types].xml
                zos.putNextEntry(ZipEntry("[Content_Types].xml"))
                zos.write("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/></Types>""".toByteArray())
                zos.closeEntry()

                // _rels/.rels
                zos.putNextEntry(ZipEntry("_rels/.rels"))
                zos.write("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>""".toByteArray())
                zos.closeEntry()

                // word/document.xml
                zos.putNextEntry(ZipEntry("word/document.xml"))
                val docXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>${bodyBuilder}<w:sectPr/></w:body></w:document>"""
                zos.write(docXml.toByteArray())
                zos.closeEntry()
            }

            shareFile(context, file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        } catch (e: Exception) {
            Toast.makeText(context, "Word Export Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Open / Share Assessment Document"))
    }
}