package com.daywise.app.data.repository

import com.daywise.app.data.local.BudgetDao
import com.daywise.app.data.local.DebtDao
import com.daywise.app.data.local.ExpenseDao
import com.daywise.app.data.model.DebtEntity
import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.data.model.MonthlyBudgetEntity
import com.daywise.app.domain.util.BackupHelper
import com.daywise.app.domain.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.time.YearMonth

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val debtDao: DebtDao
) {

    // --- EXPENSES ---

    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpensesFlow()

    fun getRecentExpensesFlow(limit: Int = 5): Flow<List<ExpenseEntity>> =
        expenseDao.getRecentExpensesFlow(limit)

    fun getExpensesForMonthFlow(yearMonth: YearMonth): Flow<List<ExpenseEntity>> {
        val start = DateUtils.getStartOfMonthMillis(yearMonth)
        val end = DateUtils.getEndOfMonthMillis(yearMonth)
        return expenseDao.getExpensesBetweenFlow(start, end)
    }

    fun getExpensesForDayFlow(dayMillis: Long): Flow<List<ExpenseEntity>> {
        val start = DateUtils.getStartOfDayMillis(dayMillis)
        val end = DateUtils.getEndOfDayMillis(dayMillis)
        return expenseDao.getExpensesBetweenFlow(start, end)
    }

    fun getTodayTotalSpentFlow(): Flow<Long> {
        val today = DateUtils.todayMillis()
        val start = DateUtils.getStartOfDayMillis(today)
        val end = DateUtils.getEndOfDayMillis(today)
        return expenseDao.getTotalSpentBetweenFlow(start, end)
    }

    fun getTodayExpenseCountFlow(): Flow<Int> {
        val today = DateUtils.todayMillis()
        val start = DateUtils.getStartOfDayMillis(today)
        val end = DateUtils.getEndOfDayMillis(today)
        return expenseDao.getCountBetweenFlow(start, end)
    }

    fun getMonthTotalSpentFlow(yearMonth: YearMonth): Flow<Long> {
        val start = DateUtils.getStartOfMonthMillis(yearMonth)
        val end = DateUtils.getEndOfMonthMillis(yearMonth)
        return expenseDao.getTotalSpentBetweenFlow(start, end)
    }

    suspend fun insertExpense(amountPaisa: Long, category: String, dateMillis: Long, note: String): Long {
        val expense = ExpenseEntity(
            amountPaisa = amountPaisa,
            category = category,
            dateMillis = dateMillis,
            note = note.trim()
        )
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(id: Long, amountPaisa: Long, category: String, dateMillis: Long, note: String) {
        val existing = expenseDao.getExpenseById(id)
        if (existing != null) {
            val updated = existing.copy(
                amountPaisa = amountPaisa,
                category = category,
                dateMillis = dateMillis,
                note = note.trim()
            )
            expenseDao.updateExpense(updated)
        }
    }

    suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    // --- BUDGETS ---

    fun getBudgetForMonthFlow(yearMonth: YearMonth): Flow<MonthlyBudgetEntity?> {
        val key = DateUtils.formatYearMonthKey(yearMonth)
        return budgetDao.getBudgetForMonthFlow(key)
    }

    suspend fun setBudgetForMonth(yearMonth: YearMonth, budgetPaisa: Long) {
        val key = DateUtils.formatYearMonthKey(yearMonth)
        budgetDao.setBudget(
            MonthlyBudgetEntity(
                yearMonth = key,
                budgetPaisa = budgetPaisa
            )
        )
    }

    suspend fun removeBudgetForMonth(yearMonth: YearMonth) {
        val key = DateUtils.formatYearMonthKey(yearMonth)
        budgetDao.deleteBudget(key)
    }

    // --- DEBTS (Lending & Borrowing) ---

    fun getAllDebtsFlow(): Flow<List<DebtEntity>> = debtDao.getAllDebtsFlow()

    fun getActiveLentTotalFlow(): Flow<Long> = debtDao.getActiveLentTotalFlow()

    fun getActiveBorrowedTotalFlow(): Flow<Long> = debtDao.getActiveBorrowedTotalFlow()

    fun getActiveDebtCountFlow(): Flow<Int> = debtDao.getActiveDebtCountFlow()

    suspend fun insertDebt(
        personName: String,
        amountPaisa: Long,
        type: String,
        dateMillis: Long?,
        dueDateMillis: Long?,
        note: String
    ): Long {
        val debt = DebtEntity(
            personName = personName.trim(),
            amountPaisa = amountPaisa,
            type = type,
            dateMillis = dateMillis,
            dueDateMillis = dueDateMillis,
            note = note.trim()
        )
        return debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(
        id: Long,
        personName: String,
        amountPaisa: Long,
        type: String,
        dateMillis: Long?,
        dueDateMillis: Long?,
        note: String
    ) {
        val existing = debtDao.getDebtById(id)
        if (existing != null) {
            val updated = existing.copy(
                personName = personName.trim(),
                amountPaisa = amountPaisa,
                type = type,
                dateMillis = dateMillis,
                dueDateMillis = dueDateMillis,
                note = note.trim()
            )
            debtDao.updateDebt(updated)
        }
    }

    suspend fun toggleDebtSettled(id: Long, currentSettled: Boolean) {
        val newStatus = !currentSettled
        val settledAt = if (newStatus) System.currentTimeMillis() else null
        debtDao.updateSettledStatus(id, newStatus, settledAt)
    }

    suspend fun deleteDebt(id: Long) {
        debtDao.deleteDebtById(id)
    }

    // --- BACKUP & CSV EXPORT ---

    suspend fun exportBackupJson(outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val expenses = expenseDao.getAllExpenses()
        val budgets = budgetDao.getAllBudgets()
        val debts = debtDao.getAllDebts()
        BackupHelper.exportToJson(outputStream, expenses, budgets, debts)
    }

    suspend fun exportMonthCsv(outputStream: OutputStream, yearMonth: YearMonth) = withContext(Dispatchers.IO) {
        val start = DateUtils.getStartOfMonthMillis(yearMonth)
        val end = DateUtils.getEndOfMonthMillis(yearMonth)
        val expenses = expenseDao.getExpensesBetween(start, end)
        BackupHelper.exportToCsv(outputStream, expenses)
    }

    suspend fun exportAllCsv(outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val expenses = expenseDao.getAllExpenses()
        BackupHelper.exportToCsv(outputStream, expenses)
    }

    suspend fun exportDebtsCsv(outputStream: OutputStream) = withContext(Dispatchers.IO) {
        val debts = debtDao.getAllDebts()
        BackupHelper.exportDebtsToCsv(outputStream, debts)
    }

    data class RestoreResult(
        val totalInFile: Int,
        val newImported: Int,
        val duplicatesSkipped: Int,
        val budgetsImported: Int,
        val debtsImported: Int
    )

    suspend fun restoreFromJson(inputStream: InputStream): RestoreResult = withContext(Dispatchers.IO) {
        val backupData = BackupHelper.parseBackupJson(inputStream)
        val existingExpenses = expenseDao.getAllExpenses()
        val existingDebts = debtDao.getAllDebts()

        val nonDuplicateExpenses = BackupHelper.findNonDuplicateExpenses(
            existingExpenses = existingExpenses,
            importedExpenses = backupData.expenses
        )

        if (nonDuplicateExpenses.isNotEmpty()) {
            expenseDao.insertExpenses(nonDuplicateExpenses)
        }

        if (backupData.budgets.isNotEmpty()) {
            budgetDao.insertBudgets(backupData.budgets)
        }

        val nonDuplicateDebts = BackupHelper.findNonDuplicateDebts(
            existingDebts = existingDebts,
            importedDebts = backupData.debts
        )

        if (nonDuplicateDebts.isNotEmpty()) {
            debtDao.insertDebts(nonDuplicateDebts)
        }

        RestoreResult(
            totalInFile = backupData.expenses.size + backupData.debts.size,
            newImported = nonDuplicateExpenses.size,
            duplicatesSkipped = (backupData.expenses.size - nonDuplicateExpenses.size) + (backupData.debts.size - nonDuplicateDebts.size),
            budgetsImported = backupData.budgets.size,
            debtsImported = nonDuplicateDebts.size
        )
    }

    suspend fun getStorageStatistics(): StorageStats = withContext(Dispatchers.IO) {
        val count = expenseDao.getTotalExpenseCount()
        val budgets = budgetDao.getAllBudgets().size
        val debts = debtDao.getTotalDebtCount()
        StorageStats(expenseCount = count, budgetCount = budgets, debtCount = debts)
    }

    data class StorageStats(
        val expenseCount: Int,
        val budgetCount: Int,
        val debtCount: Int
    )
}
