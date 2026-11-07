package com.daywise.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Monthly budget preference stored in Room.
 * yearMonth is formatted as "YYYY-MM" (e.g. "2026-11").
 * budgetPaisa is in integer paisa (100 paisa = 1 BDT ৳).
 */
@Serializable
@Entity(tableName = "monthly_budgets")
data class MonthlyBudgetEntity(
    @PrimaryKey
    @ColumnInfo(name = "year_month")
    val yearMonth: String,

    @ColumnInfo(name = "budget_paisa")
    val budgetPaisa: Long
)
