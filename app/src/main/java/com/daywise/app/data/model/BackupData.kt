package com.daywise.app.data.model

import kotlinx.serialization.Serializable

/**
 * Backup structure for exporting and restoring user data safely.
 */
@Serializable
data class BackupData(
    val version: Int = 2,
    val exportedAt: Long = System.currentTimeMillis(),
    val app: String = "Daywise",
    val expenses: List<ExpenseEntity> = emptyList(),
    val budgets: List<MonthlyBudgetEntity> = emptyList(),
    val debts: List<DebtEntity> = emptyList()
)
