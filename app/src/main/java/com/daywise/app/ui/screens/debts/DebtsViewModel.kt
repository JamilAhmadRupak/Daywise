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

data class DebtsUiState(
    val totalLentPaisa: Long = 0L,
    val totalBorrowedPaisa: Long = 0L,
    val filteredDebts: List<DebtEntity> = emptyList(),
    val totalCount: Int = 0,
    val activeFilter: DebtFilter = DebtFilter.ALL,
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
        _activeFilter
    ) { query, filter ->
        Pair(query, filter)
    }

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
    ) { allDebts, (activeLent, activeBorrowed), (query, filter), dialogs ->
        val filtered = allDebts.filter { debt ->
            val matchesFilter = when (filter) {
                DebtFilter.ALL -> !debt.isSettled
                DebtFilter.LENT -> !debt.isSettled && debt.type == DebtType.LENT.name
                DebtFilter.BORROWED -> !debt.isSettled && debt.type == DebtType.BORROWED.name
                DebtFilter.SETTLED -> debt.isSettled
            }
            val matchesQuery = if (query.isBlank()) true else {
                debt.personName.contains(query, ignoreCase = true) ||
                    debt.note.contains(query, ignoreCase = true) ||
                    (debt.amountPaisa / 100).toString().contains(query)
            }
            matchesFilter && matchesQuery
        }

        DebtsUiState(
            totalLentPaisa = activeLent,
            totalBorrowedPaisa = activeBorrowed,
            filteredDebts = filtered,
            totalCount = filtered.size,
            activeFilter = filter,
            searchQuery = query,
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
