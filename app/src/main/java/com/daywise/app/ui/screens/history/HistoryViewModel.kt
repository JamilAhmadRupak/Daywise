package com.daywise.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daywise.app.data.model.ExpenseCategory
import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.data.repository.ExpenseRepository
import com.daywise.app.domain.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DayExpenseGroup(
    val dayMillis: Long,
    val date: LocalDate,
    val displayDate: String,
    val totalPaisa: Long,
    val expenses: List<ExpenseEntity>
)

data class HistoryUiState(
    val groupedExpenses: List<DayExpenseGroup> = emptyList(),
    val totalExpensesCount: Int = 0,
    val overallTotalPaisa: Long = 0L,
    val searchQuery: String = "",
    val selectedCategoryFilter: ExpenseCategory? = null,
    val isAddExpenseOpen: Boolean = false,
    val editingExpense: ExpenseEntity? = null,
    val expenseToDelete: ExpenseEntity? = null
)

class HistoryViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _categoryFilter = MutableStateFlow<ExpenseCategory?>(null)
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

    private val filterStateFlow = combine(
        _searchQuery,
        _categoryFilter
    ) { query, categoryFilter ->
        Pair(query, categoryFilter)
    }

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.getAllExpensesFlow(),
        filterStateFlow,
        _dialogState
    ) { allExpenses, filters, dialogs ->
        val (query, categoryFilter) = filters

        // Apply search and category filter
        val filtered = allExpenses.filter { expense ->
            val matchesCategory = categoryFilter == null || expense.category.equals(categoryFilter.name, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                expense.note.contains(query, ignoreCase = true) ||
                    expense.category.contains(query, ignoreCase = true) ||
                    (expense.amountPaisa / 100).toString().contains(query)
            }
            matchesCategory && matchesQuery
        }

        // Group by day (normalized epoch day)
        val groups = filtered.groupBy { DateUtils.getStartOfDayMillis(it.dateMillis) }
            .map { (dayMillis, list) ->
                DayExpenseGroup(
                    dayMillis = dayMillis,
                    date = DateUtils.toLocalDate(dayMillis),
                    displayDate = DateUtils.formatDisplayDate(dayMillis),
                    totalPaisa = list.sumOf { it.amountPaisa },
                    expenses = list.sortedWith(compareByDescending<ExpenseEntity> { it.dateMillis }.thenByDescending { it.createdAt })
                )
            }.sortedByDescending { it.dayMillis }

        HistoryUiState(
            groupedExpenses = groups,
            totalExpensesCount = filtered.size,
            overallTotalPaisa = filtered.sumOf { it.amountPaisa },
            searchQuery = query,
            selectedCategoryFilter = categoryFilter,
            isAddExpenseOpen = dialogs.isAddExpenseOpen,
            editingExpense = dialogs.editingExpense,
            expenseToDelete = dialogs.expenseToDelete
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilterChanged(category: ExpenseCategory?) {
        _categoryFilter.value = category
    }

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
            } else {
                repository.insertExpense(
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

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(repository) as T
        }
    }
}
