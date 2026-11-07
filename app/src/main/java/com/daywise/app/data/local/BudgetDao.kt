package com.daywise.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daywise.app.data.model.MonthlyBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setBudget(budget: MonthlyBudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<MonthlyBudgetEntity>)

    @Query("SELECT * FROM monthly_budgets WHERE year_month = :yearMonth LIMIT 1")
    fun getBudgetForMonthFlow(yearMonth: String): Flow<MonthlyBudgetEntity?>

    @Query("SELECT * FROM monthly_budgets WHERE year_month = :yearMonth LIMIT 1")
    suspend fun getBudgetForMonth(yearMonth: String): MonthlyBudgetEntity?

    @Query("SELECT * FROM monthly_budgets")
    suspend fun getAllBudgets(): List<MonthlyBudgetEntity>

    @Query("DELETE FROM monthly_budgets WHERE year_month = :yearMonth")
    suspend fun deleteBudget(yearMonth: String)
}
