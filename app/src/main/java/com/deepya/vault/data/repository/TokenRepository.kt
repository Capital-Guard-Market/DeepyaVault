package com.deepya.vault.data.repository

import com.deepya.vault.data.dao.TokenDao
import com.deepya.vault.data.entity.TokenLedgerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TokenRepository(private val tokenDao: TokenDao) {

    val liveBalance: Flow<Int> = tokenDao.getLiveBalance()
    val transactionHistory: Flow<List<TokenLedgerEntity>> = tokenDao.getTransactionHistory()

    suspend fun getCurrentBalance(): Int = withContext(Dispatchers.IO) {
        tokenDao.getCurrentBalance()
    }

    suspend fun grantWelcomeBonus(bonusAmount: Int = 100): Boolean = withContext(Dispatchers.IO) {
        if (!tokenDao.isWelcomeBonusClaimed()) {
            val transaction = TokenLedgerEntity(
                delta = bonusAmount,
                transactionType = "WELCOME_BONUS",
                referenceId = "SYSTEM_INIT",
                description = "Initial signup bonus credits",
                signatureHmac = "DEV_MOCK_SIGNATURE"
            )
            tokenDao.insertTransaction(transaction) > 0
        } else {
            false
        }
    }

    suspend fun unlockChapter(chapterId: String, cost: Int): Result<Unit> = withContext(Dispatchers.IO) {
        if (tokenDao.isChapterUnlocked(chapterId)) {
            return@withContext Result.success(Unit)
        }

        val currentBalance = tokenDao.getCurrentBalance()
        if (currentBalance < cost) {
            return@withContext Result.failure(IllegalStateException("Insufficient token balance"))
        }

        val deduction = TokenLedgerEntity(
            delta = -cost,
            transactionType = "CHAPTER_UNLOCK",
            referenceId = chapterId,
            description = "Unlocked chapter $chapterId",
            signatureHmac = "DEV_MOCK_SIGNATURE"
        )
        tokenDao.insertTransaction(deduction)
        Result.success(Unit)
    }

    suspend fun awardTokens(amount: Int, reason: String, referenceId: String? = null): Long = withContext(Dispatchers.IO) {
        val transaction = TokenLedgerEntity(
            delta = amount,
            transactionType = "REWARD",
            referenceId = referenceId,
            description = reason,
            signatureHmac = "DEV_MOCK_SIGNATURE"
        )
        tokenDao.insertTransaction(transaction)
    }
}