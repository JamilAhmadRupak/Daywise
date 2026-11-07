package com.daywise.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daywise.app.data.model.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>): List<Long>

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: Long)

    @Query("SELECT * FROM debts WHERE id = :id LIMIT 1")
    suspend fun getDebtById(id: Long): DebtEntity?

    @Query("SELECT * FROM debts ORDER BY is_settled ASC, created_at DESC")
    fun getAllDebtsFlow(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts ORDER BY is_settled ASC, created_at DESC")
    suspend fun getAllDebts(): List<DebtEntity>

    @Query("SELECT COALESCE(SUM(amount_paisa), 0) FROM debts WHERE type = 'LENT' AND is_settled = 0")
    fun getActiveLentTotalFlow(): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount_paisa), 0) FROM debts WHERE type = 'BORROWED' AND is_settled = 0")
    fun getActiveBorrowedTotalFlow(): Flow<Long>

    @Query("SELECT COUNT(*) FROM debts WHERE is_settled = 0")
    fun getActiveDebtCountFlow(): Flow<Int>

    @Query("UPDATE debts SET is_settled = :isSettled, settled_at = :settledAt WHERE id = :id")
    suspend fun updateSettledStatus(id: Long, isSettled: Boolean, settledAt: Long?)

    @Query("SELECT COUNT(*) FROM debts")
    suspend fun getTotalDebtCount(): Int
}
