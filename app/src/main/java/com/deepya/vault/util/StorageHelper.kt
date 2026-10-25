package com.deepya.vault.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object StorageHelper {

    fun getExportDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "GeneratedPapers")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }
}