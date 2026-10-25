package com.deepya.vault.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deepya.vault.data.entity.TokenLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: TokenLedgerEntity): Long

    @Query("SELECT COALESCE(SUM(delta), 0) FROM token_ledger")
    fun getLiveBalance(): Flow<Int>

    @Query("SELECT COALESCE(SUM(delta), 0) FROM token_ledger")
    suspend fun getCurrentBalance(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM token_ledger WHERE transactionType = 'WELCOME_BONUS' LIMIT 1)")
    suspend fun isWelcomeBonusClaimed(): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM token_ledger WHERE transactionType = 'CHAPTER_UNLOCK' AND referenceId = :chapterId LIMIT 1)")
    suspend fun isChapterUnlocked(chapterId: String): Boolean

    @Query("SELECT * FROM token_ledger ORDER BY timestamp DESC")
    fun getTransactionHistory(): Flow<List<TokenLedgerEntity>>
}