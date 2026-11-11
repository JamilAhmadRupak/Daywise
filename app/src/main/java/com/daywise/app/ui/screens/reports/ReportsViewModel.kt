package com.daywise.app.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daywise.app.data.model.ExpenseCategory
import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.data.repository.ExpenseRepository
import com.daywise.app.domain.util.DateUtils
import com.daywise.app.ui.components.CategorySpending
import com.daywise.app.ui.components.DaySpending
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import android.widget.Toast
import java.time.LocalDate
import java.time.YearMonth

data class ReportsUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val displayMonthName: String = "",
    val totalSpendingPaisa: Long = 0L,
    val totalTransactionsCount: Int = 0,
    val averageDailySpendingPaisa: Long = 0L,
    val highestSpendingDay: LocalDate? = null,
    val highestSpendingAmountPaisa: Long = 0L,
    val dailySpendingList: List<DaySpending> = emptyList(),
    val categoryBreakdown: List<CategorySpending> = emptyList(),
    val selectedDayFilter: Int? = null,
    val dayDrilldownExpenses: List<ExpenseEntity> = emptyList(),
    val monthlyBudgetPaisa: Long? = null,
    val isBudgetDialogOpen: Boolean = false,
    val editingExpense: ExpenseEntity? = null,
    val isAddExpenseOpen: Boolean = false,
    val expenseToDelete: ExpenseEntity? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDay = MutableStateFlow<Int?>(null)
    private val _isBudgetDialogOpen = MutableStateFlow(false)
    private val _dialogState = MutableStateFlow(
        DialogState(
            editingExpense = null,
            isAddOpen = false,
            expenseToDelete = null
        )
    )

    private data class DialogState(
        val editingExpense: ExpenseEntity?,
        val isAddOpen: Boolean,
        val expenseToDelete: ExpenseEntity?
    )

    val uiState: StateFlow<ReportsUiState> = _selectedMonth.flatMapLatest { month ->
        val dataFlow = combine(
            repository.getExpensesForMonthFlow(month),
            repository.getBudgetForMonthFlow(month)
        ) { expenses, budgetEntity ->
            Pair(expenses, budgetEntity)
        }

        combine(
            dataFlow,
            _selectedDay,
            _isBudgetDialogOpen,
            _dialogState
        ) { (expenses, budgetEntity), selectedDay, isBudgetOpen, dialogs ->
            val totalSpending = expenses.sumOf { it.amountPaisa }
            val count = expenses.size

            // Days in month calculation
            val daysInMonth = month.lengthOfMonth()
            val elapsedDays = DateUtils.getElapsedDaysInMonth(month)
            val avgDaily = if (elapsedDays > 0) totalSpending / elapsedDays else 0L

            // Group by day of month for bar chart
            val expensesByDay = expenses.groupBy { DateUtils.toLocalDate(it.dateMillis).dayOfMonth }
            val dailyList = (1..daysInMonth).map { dayNum ->
                val dayExpenses = expensesByDay[dayNum] ?: emptyList()
                DaySpending(
                    dayOfMonth = dayNum,
                    amountPaisa = dayExpenses.sumOf { it.amountPaisa },
                    date = month.atDay(dayNum)
                )
            }

            // Highest spending day
            val highestDaySpending = dailyList.filter { it.amountPaisa > 0 }.maxByOrNull { it.amountPaisa }

            // Category breakdown for donut chart
            val expensesByCategory = expenses.groupBy { it.category }
            val categoryList = expensesByCategory.map { (catName, list) ->
                val catModel = com.daywise.app.data.model.CategoryModel.resolve(catName)
                val catSum = list.sumOf { it.amountPaisa }
                val pct = if (totalSpending > 0L) (catSum.toFloat() / totalSpending) * 100f else 0f
                CategorySpending(
                    category = ExpenseCategory.fromName(catName),
                    amountPaisa = catSum,
                    percentage = pct,
                    displayName = catModel.displayName,
                    color = catModel.color,
                    icon = catModel.icon
                )
            }.sortedByDescending { it.amountPaisa }

            // Selected day drilldown expenses
            val drilldown = if (selectedDay != null) {
                expenses.filter { DateUtils.toLocalDate(it.dateMillis).dayOfMonth == selectedDay }
                    .sortedWith(compareByDescending<ExpenseEntity> { it.dateMillis }.thenByDescending { it.createdAt })
            } else {
                emptyList()
            }

            ReportsUiState(
                currentMonth = month,
                displayMonthName = DateUtils.formatMonthYear(month),
                totalSpendingPaisa = totalSpending,
                totalTransactionsCount = count,
                averageDailySpendingPaisa = avgDaily,
                highestSpendingDay = highestDaySpending?.date,
                highestSpendingAmountPaisa = highestDaySpending?.amountPaisa ?: 0L,
                dailySpendingList = dailyList,
                categoryBreakdown = categoryList,
                selectedDayFilter = selectedDay,
                dayDrilldownExpenses = drilldown,
                monthlyBudgetPaisa = budgetEntity?.budgetPaisa,
                isBudgetDialogOpen = isBudgetOpen,
                editingExpense = dialogs.editingExpense,
                isAddExpenseOpen = dialogs.isAddOpen,
                expenseToDelete = dialogs.expenseToDelete
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState(displayMonthName = DateUtils.formatMonthYear(YearMonth.now()))
    )

    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
        _selectedDay.value = null
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
        _selectedDay.value = null
    }

    fun selectDay(day: Int) {
        if (_selectedDay.value == day) {
            _selectedDay.value = null // Toggle off
        } else {
            _selectedDay.value = day
        }
    }

    fun openBudgetDialog() {
        _isBudgetDialogOpen.value = true
    }

    fun closeBudgetDialog() {
        _isBudgetDialogOpen.value = false
    }

    fun saveBudget(paisa: Long) {
        viewModelScope.launch {
            repository.setBudgetForMonth(_selectedMonth.value, paisa)
            closeBudgetDialog()
        }
    }

    fun removeBudget() {
        viewModelScope.launch {
            repository.removeBudgetForMonth(_selectedMonth.value)
            closeBudgetDialog()
        }
    }

    fun openEditExpense(expense: ExpenseEntity) {
        _dialogState.value = _dialogState.value.copy(
            editingExpense = expense,
            isAddOpen = true
        )
    }

    fun closeAddEditSheet() {
        _dialogState.value = _dialogState.value.copy(
            editingExpense = null,
            isAddOpen = false
        )
    }

    fun saveExpense(amountPaisa: Long, category: String, dateMillis: Long, note: String) {
        viewModelScope.launch {
            val editing = _dialogState.value.editingExpense
            if (editing != null) {
                repository.updateExpense(
                    id = editing.id,
                    amountPaisa = amountPaisa,
                    category = category,
                    dateMillis = dateMillis,
                    note = note
                )
            }
            closeAddEditSheet()
        }
    }

    fun saveExpense(amountPaisa: Long, category: ExpenseCategory, dateMillis: Long, note: String) {
        saveExpense(amountPaisa, category.name, dateMillis, note)
    }

    fun confirmDeleteExpense(expense: ExpenseEntity) {
        _dialogState.value = _dialogState.value.copy(expenseToDelete = expense)
    }

    fun cancelDeleteExpense() {
        _dialogState.value = _dialogState.value.copy(expenseToDelete = null)
    }

    fun deleteConfirmed() {
        val target = _dialogState.value.expenseToDelete ?: return
        viewModelScope.launch {
            repository.deleteExpense(target.id)
            cancelDeleteExpense()
        }
    }

    fun exportMonthCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    repository.exportMonthCsv(outputStream, _selectedMonth.value)
                }
                Toast.makeText(context, "Monthly Report Exported!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportsViewModel(repository) as T
        }
    }
}
