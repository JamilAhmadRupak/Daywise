package com.daywise.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daywise.app.data.model.ExpenseCategory
import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth

data class HomeUiState(
    val todayTotalPaisa: Long = 0L,
    val todayExpenseCount: Int = 0,
    val currentMonthTotalPaisa: Long = 0L,
    val monthlyBudgetPaisa: Long? = null,
    val recentExpenses: List<ExpenseEntity> = emptyList(),
    val isAddExpenseOpen: Boolean = false,
    val editingExpense: ExpenseEntity? = null,
    val expenseToDelete: ExpenseEntity? = null
) {
    val budgetProgress: Float
        get() {
            val budget = monthlyBudgetPaisa ?: return 0f
            if (budget <= 0L) return 0f
            return (currentMonthTotalPaisa.toFloat() / budget).coerceIn(0f, 1.5f)
        }

    val remainingBudgetPaisa: Long?
        get() = monthlyBudgetPaisa?.let { it - currentMonthTotalPaisa }

    val isOverBudget: Boolean
        get() = (monthlyBudgetPaisa != null) && (currentMonthTotalPaisa > monthlyBudgetPaisa)
}

class HomeViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val currentMonth = YearMonth.now()

    private val _dialogState = MutableStateFlow(
        DialogState(
            isAddExpenseOpen = false,
            editingExpense = null,
            expenseToDelete = null
        )
    )

    private data class DialogState(
        val isAddExpenseOpen: Boolean,
        val editingExpense: ExpenseEntity?,
        val expenseToDelete: ExpenseEntity?
    )

    private val baseMetricsFlow = combine(
        repository.getTodayTotalSpentFlow(),
        repository.getTodayExpenseCountFlow(),
        repository.getMonthTotalSpentFlow(currentMonth),
        repository.getBudgetForMonthFlow(currentMonth)
    ) { todayTotal, todayCount, monthTotal, budgetEntity ->
        BaseMetrics(
            todayTotal = todayTotal,
            todayCount = todayCount,
            monthTotal = monthTotal,
            budgetPaisa = budgetEntity?.budgetPaisa
        )
    }

    private data class BaseMetrics(
        val todayTotal: Long,
        val todayCount: Int,
        val monthTotal: Long,
        val budgetPaisa: Long?
    )

    val uiState: StateFlow<HomeUiState> = combine(
        baseMetricsFlow,
        repository.getRecentExpensesFlow(limit = 6),
        _dialogState
    ) { metrics, recent, dialogs ->
        HomeUiState(
            todayTotalPaisa = metrics.todayTotal,
            todayExpenseCount = metrics.todayCount,
            currentMonthTotalPaisa = metrics.monthTotal,
            monthlyBudgetPaisa = metrics.budgetPaisa,
            recentExpenses = recent,
            isAddExpenseOpen = dialogs.isAddExpenseOpen,
            editingExpense = dialogs.editingExpense,
            expenseToDelete = dialogs.expenseToDelete
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun openAddExpense() {
        _dialogState.value = _dialogState.value.copy(
            isAddExpenseOpen = true,
            editingExpense = null
        )
    }

    fun openEditExpense(expense: ExpenseEntity) {
        _dialogState.value = _dialogState.value.copy(
            isAddExpenseOpen = true,
            editingExpense = expense
        )
    }

    fun closeAddEditSheet() {
        _dialogState.value = _dialogState.value.copy(
            isAddExpenseOpen = false,
            editingExpense = null
        )
    }

    fun saveExpense(
        amountPaisa: Long,
        category: ExpenseCategory,
        dateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            val editing = _dialogState.value.editingExpense
            if (editing != null) {
                repository.updateExpense(
                    id = editing.id,
                    amountPaisa = amountPaisa,
                    category = category.name,
                    dateMillis = dateMillis,
                    note = note
                )
            } else {
                repository.insertExpense(
                    amountPaisa = amountPaisa,
                    category = category.name,
                    dateMillis = dateMillis,
                    note = note
                )
            }
            closeAddEditSheet()
        }
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

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
