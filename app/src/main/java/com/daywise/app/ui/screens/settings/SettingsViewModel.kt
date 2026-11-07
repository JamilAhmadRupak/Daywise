package com.daywise.app.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.daywise.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

data class SettingsUiState(
    val totalExpenseCount: Int = 0,
    val totalBudgetCount: Int = 0,
    val isLoading: Boolean = false
)

sealed interface SettingsEvent {
    data class ShowToast(val message: String) : SettingsEvent
}

class SettingsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SettingsEvent>()
    val eventFlow: SharedFlow<SettingsEvent> = _eventFlow.asSharedFlow()

    init {
        loadStorageStats()
    }

    fun loadStorageStats() {
        viewModelScope.launch {
            val stats = repository.getStorageStatistics()
            _uiState.value = _uiState.value.copy(
                totalExpenseCount = stats.expenseCount,
                totalBudgetCount = stats.budgetCount
            )
        }
    }

    fun exportBackupJson(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    repository.exportBackupJson(outputStream)
                }
                _eventFlow.emit(SettingsEvent.ShowToast("JSON backup created successfully!"))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowToast("Failed to export backup: ${e.message}"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun restoreBackupJson(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    repository.restoreFromJson(inputStream)
                }
                if (result != null) {
                    val msg = "Restored: ${result.newImported} imported, ${result.duplicatesSkipped} duplicates skipped"
                    _eventFlow.emit(SettingsEvent.ShowToast(msg))
                    loadStorageStats()
                } else {
                    _eventFlow.emit(SettingsEvent.ShowToast("Unable to read backup file."))
                }
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowToast("Restore failed: ${e.message}"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun exportMonthCsv(context: Context, uri: Uri, yearMonth: YearMonth = YearMonth.now()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    repository.exportMonthCsv(outputStream, yearMonth)
                }
                _eventFlow.emit(SettingsEvent.ShowToast("CSV exported successfully!"))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowToast("Failed to export CSV: ${e.message}"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun exportAllCsv(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    repository.exportAllCsv(outputStream)
                }
                _eventFlow.emit(SettingsEvent.ShowToast("All expenses exported as CSV!"))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowToast("Failed to export CSV: ${e.message}"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(repository) as T
        }
    }
}
