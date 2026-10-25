package com.deepya.vault.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "token_ledger",
    indices = [
        Index(value = ["transactionType"]),
        Index(value = ["referenceId"])
    ]
)
data class TokenLedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0L,

    // Token delta: positive for credits (+25 bonus, +110 pack), negative for debits (-20 unlock, -2 export)
    val delta: Int,

    // Category: "WELCOME_BONUS", "CHAPTER_UNLOCK", "EXAM_PDF_EXPORT", "PACK_PURCHASE", "REWARD_STREAK"
    val transactionType: String,

    // Context reference: chapterId for unlocks, exam timestamp/UUID for exports, or Play Store orderId
    val referenceId: String? = null,

    // Readable label for UI history (e.g., "Unlocked CBSE Class 12 Partnership: -20 Tokens")
    val description: String = "",

    // Creation timestamp
    val timestamp: Long = System.currentTimeMillis(),

    // Cryptographic signature sealing the transaction row to prevent local SQLite tampering
    val signatureHmac: String
)