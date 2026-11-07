package com.daywise.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.daywise.app.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>): List<Long>

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: Long): ExpenseEntity?

    @Query("SELECT * FROM expenses ORDER BY date_millis DESC, created_at DESC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses ORDER BY date_millis DESC, created_at DESC")
    suspend fun getAllExpenses(): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE date_millis >= :startMillis AND date_millis <= :endMillis ORDER BY date_millis DESC, created_at DESC")
    fun getExpensesBetweenFlow(startMillis: Long, endMillis: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date_millis >= :startMillis AND date_millis <= :endMillis ORDER BY date_millis DESC, created_at DESC")
    suspend fun getExpensesBetween(startMillis: Long, endMillis: Long): List<ExpenseEntity>

    @Query("SELECT COALESCE(SUM(amount_paisa), 0) FROM expenses WHERE date_millis >= :startMillis AND date_millis <= :endMillis")
    fun getTotalSpentBetweenFlow(startMillis: Long, endMillis: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM expenses WHERE date_millis >= :startMillis AND date_millis <= :endMillis")
    fun getCountBetweenFlow(startMillis: Long, endMillis: Long): Flow<Int>

    @Query("SELECT * FROM expenses ORDER BY date_millis DESC, created_at DESC LIMIT :limit")
    fun getRecentExpensesFlow(limit: Int): Flow<List<ExpenseEntity>>

    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun getTotalExpenseCount(): Int
}
