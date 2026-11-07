package com.daywise.app.domain.util

import com.daywise.app.data.model.BackupData
import com.daywise.app.data.model.DebtEntity
import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.data.model.MonthlyBudgetEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * Handles JSON backup, restore with deduplication, and CSV generation.
 */
object BackupHelper {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun exportToJson(
        outputStream: OutputStream,
        expenses: List<ExpenseEntity>,
        budgets: List<MonthlyBudgetEntity>,
        debts: List<DebtEntity> = emptyList()
    ) {
        val backupData = BackupData(
            version = 2,
            exportedAt = System.currentTimeMillis(),
            app = "Daywise",
            expenses = expenses,
            budgets = budgets,
            debts = debts
        )
        val jsonString = json.encodeToString(backupData)
        outputStream.bufferedWriter().use { writer ->
            writer.write(jsonString)
            writer.flush()
        }
    }

    fun parseBackupJson(inputStream: InputStream): BackupData {
        val content = inputStream.bufferedReader().use { it.readText() }
        return json.decodeFromString<BackupData>(content)
    }

    fun findNonDuplicateExpenses(
        existingExpenses: List<ExpenseEntity>,
        importedExpenses: List<ExpenseEntity>
    ): List<ExpenseEntity> {
        val existingKeys = existingExpenses.map {
            createExpenseDeduplicationKey(it)
        }.toHashSet()

        return importedExpenses.filter { incoming ->
            val key = createExpenseDeduplicationKey(incoming)
            if (existingKeys.contains(key)) {
                false
            } else {
                existingKeys.add(key)
                true
            }
        }.map {
            it.copy(id = 0L)
        }
    }

    private fun createExpenseDeduplicationKey(e: ExpenseEntity): String {
        val dayStart = DateUtils.getStartOfDayMillis(e.dateMillis)
        return "${e.amountPaisa}|${e.category.trim().uppercase()}|$dayStart|${e.note.trim()}"
    }

    fun findNonDuplicateDebts(
        existingDebts: List<DebtEntity>,
        importedDebts: List<DebtEntity>
    ): List<DebtEntity> {
        val existingKeys = existingDebts.map {
            createDebtDeduplicationKey(it)
        }.toHashSet()

        return importedDebts.filter { incoming ->
            val key = createDebtDeduplicationKey(incoming)
            if (existingKeys.contains(key)) {
                false
            } else {
                existingKeys.add(key)
                true
            }
        }.map {
            it.copy(id = 0L)
        }
    }

    private fun createDebtDeduplicationKey(d: DebtEntity): String {
        val normalizedDate = d.dateMillis?.let { DateUtils.getStartOfDayMillis(it) } ?: -1L
        return "${d.personName.trim().lowercase()}|${d.type}|${d.amountPaisa}|$normalizedDate|${d.note.trim()}"
    }

    fun exportToCsv(
        outputStream: OutputStream,
        expenses: List<ExpenseEntity>
    ) {
        outputStream.bufferedWriter().use { writer ->
            writer.write("Date,Category,Amount (BDT),Note,Created At\n")
            for (expense in expenses) {
                val dateStr = DateUtils.formatCsvDate(expense.dateMillis)
                val categoryStr = escapeCsv(expense.category)
                val amountStr = String.format(java.util.Locale.US, "%.2f", expense.amountPaisa / 100.0)
                val noteStr = escapeCsv(expense.note)
                val createdAtStr = DateUtils.formatExactDate(expense.createdAt)
                writer.write("$dateStr,$categoryStr,$amountStr,$noteStr,$createdAtStr\n")
            }
            writer.flush()
        }
    }

    fun exportDebtsToCsv(
        outputStream: OutputStream,
        debts: List<DebtEntity>
    ) {
        outputStream.bufferedWriter().use { writer ->
            writer.write("Person,Type,Amount (BDT),Date,Due Date,Status,Note\n")
            for (debt in debts) {
                val person = escapeCsv(debt.personName)
                val type = debt.type
                val amount = String.format(java.util.Locale.US, "%.2f", debt.amountPaisa / 100.0)
                val date = debt.dateMillis?.let { DateUtils.formatCsvDate(it) } ?: "No date"
                val dueDate = debt.dueDateMillis?.let { DateUtils.formatCsvDate(it) } ?: ""
                val status = if (debt.isSettled) "Settled" else "Active"
                val note = escapeCsv(debt.note)
                writer.write("$person,$type,$amount,$date,$dueDate,$status,$note\n")
            }
            writer.flush()
        }
    }

    private fun escapeCsv(value: String): String {
        val hasSpecialChars = value.contains(",") || value.contains("\"") || value.contains("\n")
        return if (hasSpecialChars) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
