package com.daywise.app.ui.screens.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daywise.app.data.model.DebtEntity
import com.daywise.app.data.model.DebtType
import com.daywise.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DebtFilter {
    ALL,
    LENT,
    BORROWED,
    SETTLED
}

enum class DebtGroupBy {
    NONE,
    PERSON,
    DATE
}

data class DebtGroup(
    val title: String,
    val items: List<DebtEntity>,
    val totalLentPaisa: Long = 0L,
    val totalBorrowedPaisa: Long = 0L,
    val lentCount: Int = 0,
    val borrowedCount: Int = 0,
    val isPersonGroup: Boolean = false,
    val isDateGroup: Boolean = false
) {
    val netPaisa: Long
        get() = totalLentPaisa - totalBorrowedPaisa

    val totalCount: Int
        get() = items.size
}

data class DebtsUiState(
    val totalLentPaisa: Long = 0L,
    val totalBorrowedPaisa: Long = 0L,
    val groupedDebts: List<DebtGroup> = emptyList(),
    val totalCount: Int = 0,
    val activeFilter: DebtFilter = DebtFilter.ALL,
    val activeGroupBy: DebtGroupBy = DebtGroupBy.NONE,
    val searchQuery: String = "",
    val isAddDebtOpen: Boolean = false,
    val editingDebt: DebtEntity? = null,
    val debtToDelete: DebtEntity? = null
) {
    val netBalancePaisa: Long
        get() = totalLentPaisa - totalBorrowedPaisa
}

class DebtsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(DebtFilter.ALL)
    private val _activeGroupBy = MutableStateFlow(DebtGroupBy.NONE)
    private val _dialogState = MutableStateFlow(
        DialogState(
            isAddDebtOpen = false,
            editingDebt = null,
            debtToDelete = null
        )
    )

    private data class DialogState(
        val isAddDebtOpen: Boolean,
        val editingDebt: DebtEntity?,
        val debtToDelete: DebtEntity?
    )

    private val filterStateFlow = combine(
        _searchQuery,
        _activeFilter,
        _activeGroupBy
    ) { query, filter, groupBy ->
        FilterState(query, filter, groupBy)
    }

    private data class FilterState(
        val query: String,
        val filter: DebtFilter,
        val groupBy: DebtGroupBy
    )

    private val statsFlow = combine(
        repository.getActiveLentTotalFlow(),
        repository.getActiveBorrowedTotalFlow()
    ) { lent, borrowed ->
        Pair(lent, borrowed)
    }

    val uiState: StateFlow<DebtsUiState> = combine(
        repository.getAllDebtsFlow(),
        statsFlow,
        filterStateFlow,
        _dialogState
    ) { allDebts, (activeLent, activeBorrowed), filterState, dialogs ->
        val filtered = allDebts.filter { debt ->
            val matchesFilter = when (filterState.filter) {
                DebtFilter.ALL -> !debt.isSettled
                DebtFilter.LENT -> !debt.isSettled && debt.type == DebtType.LENT.name
                DebtFilter.BORROWED -> !debt.isSettled && debt.type == DebtType.BORROWED.name
                DebtFilter.SETTLED -> debt.isSettled
            }
            val matchesQuery = if (filterState.query.isBlank()) true else {
                debt.personName.contains(filterState.query, ignoreCase = true) ||
                    debt.note.contains(filterState.query, ignoreCase = true) ||
                    (debt.amountPaisa / 100).toString().contains(filterState.query)
            }
            matchesFilter && matchesQuery
        }

        val grouped = when (filterState.groupBy) {
            DebtGroupBy.NONE -> {
                val lentSum = filtered.filter { it.type == DebtType.LENT.name && !it.isSettled }.sumOf { it.amountPaisa }
                val borrowedSum = filtered.filter { it.type == DebtType.BORROWED.name && !it.isSettled }.sumOf { it.amountPaisa }
                listOf(
                    DebtGroup(
                        title = "",
                        items = filtered,
                        totalLentPaisa = lentSum,
                        totalBorrowedPaisa = borrowedSum,
                        lentCount = filtered.count { it.type == DebtType.LENT.name },
                        borrowedCount = filtered.count { it.type == DebtType.BORROWED.name }
                    )
                )
            }
            DebtGroupBy.PERSON -> {
                filtered.groupBy { it.personName.trim().lowercase() }
                    .map { (_, list) ->
                        val displayName = list.first().personName.trim()
                        val lentSum = list.filter { it.type == DebtType.LENT.name && !it.isSettled }.sumOf { it.amountPaisa }
                        val borrowedSum = list.filter { it.type == DebtType.BORROWED.name && !it.isSettled }.sumOf { it.amountPaisa }
                        val displayLent = if (lentSum == 0L && list.any { it.type == DebtType.LENT.name }) {
                            list.filter { it.type == DebtType.LENT.name }.sumOf { it.amountPaisa }
                        } else lentSum
                        val displayBorrowed = if (borrowedSum == 0L && list.any { it.type == DebtType.BORROWED.name }) {
                            list.filter { it.type == DebtType.BORROWED.name }.sumOf { it.amountPaisa }
                        } else borrowedSum

                        DebtGroup(
                            title = displayName,
                            items = list.sortedWith(compareByDescending<DebtEntity> { it.dateMillis ?: it.createdAt }),
                            totalLentPaisa = displayLent,
                            totalBorrowedPaisa = displayBorrowed,
                            lentCount = list.count { it.type == DebtType.LENT.name },
                            borrowedCount = list.count { it.type == DebtType.BORROWED.name },
                            isPersonGroup = true
                        )
                    }
                    .sortedBy { it.title.lowercase() }
            }
            DebtGroupBy.DATE -> {
                filtered.groupBy { debt ->
                    debt.dateMillis?.let { ms -> com.daywise.app.domain.util.DateUtils.formatDisplayDate(ms) } ?: "Undated / Ongoing"
                }.map { (dateStr, list) ->
                    val lentSum = list.filter { it.type == DebtType.LENT.name }.sumOf { it.amountPaisa }
                    val borrowedSum = list.filter { it.type == DebtType.BORROWED.name }.sumOf { it.amountPaisa }
                    DebtGroup(
                        title = dateStr,
                        items = list.sortedByDescending { it.createdAt },
                        totalLentPaisa = lentSum,
                        totalBorrowedPaisa = borrowedSum,
                        lentCount = list.count { it.type == DebtType.LENT.name },
                        borrowedCount = list.count { it.type == DebtType.BORROWED.name },
                        isDateGroup = true
                    )
                }
            }
        }

        DebtsUiState(
            totalLentPaisa = activeLent,
            totalBorrowedPaisa = activeBorrowed,
            groupedDebts = grouped,
            totalCount = filtered.size,
            activeFilter = filterState.filter,
            activeGroupBy = filterState.groupBy,
            searchQuery = filterState.query,
            isAddDebtOpen = dialogs.isAddDebtOpen,
            editingDebt = dialogs.editingDebt,
            debtToDelete = dialogs.debtToDelete
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebtsUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChanged(filter: DebtFilter) {
        _activeFilter.value = filter
    }
    
    fun onGroupByChanged(groupBy: DebtGroupBy) {
        _activeGroupBy.value = groupBy
    }

    fun openAddDebt() {
        _dialogState.value = _dialogState.value.copy(
            isAddDebtOpen = true,
            editingDebt = null
        )
    }

    fun openEditDebt(debt: DebtEntity) {
        _dialogState.value = _dialogState.value.copy(
            isAddDebtOpen = true,
            editingDebt = debt
        )
    }

    fun closeAddEditSheet() {
        _dialogState.value = _dialogState.value.copy(
            isAddDebtOpen = false,
            editingDebt = null
        )
    }

    fun saveDebt(
        personName: String,
        amountPaisa: Long,
        type: DebtType,
        dateMillis: Long?,
        note: String
    ) {
        viewModelScope.launch {
            val editing = _dialogState.value.editingDebt
            if (editing != null) {
                repository.updateDebt(
                    id = editing.id,
                    personName = personName,
                    amountPaisa = amountPaisa,
                    type = type.name,
                    dateMillis = dateMillis,
                    dueDateMillis = editing.dueDateMillis,
                    note = note
                )
            } else {
                repository.insertDebt(
                    personName = personName,
                    amountPaisa = amountPaisa,
                    type = type.name,
                    dateMillis = dateMillis,
                    dueDateMillis = null,
                    note = note
                )
            }
            closeAddEditSheet()
        }
    }

    fun toggleDebtSettled(debt: DebtEntity) {
        viewModelScope.launch {
            repository.toggleDebtSettled(debt.id, debt.isSettled)
        }
    }

    fun confirmDeleteDebt(debt: DebtEntity) {
        _dialogState.value = _dialogState.value.copy(debtToDelete = debt)
    }

    fun cancelDeleteDebt() {
        _dialogState.value = _dialogState.value.copy(debtToDelete = null)
    }

    fun deleteConfirmed() {
        val target = _dialogState.value.debtToDelete ?: return
        viewModelScope.launch {
            repository.deleteDebt(target.id)
            cancelDeleteDebt()
        }
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DebtsViewModel(repository) as T
        }
    }
}
