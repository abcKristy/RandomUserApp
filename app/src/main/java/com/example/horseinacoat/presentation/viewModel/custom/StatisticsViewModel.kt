package com.example.horseinacoat.presentation.viewModel.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.UsersStatistics
import com.example.horseinacoat.domain.usecase.GetUsersStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getUsersStatisticsUseCase: GetUsersStatisticsUseCase
) : ViewModel() {

    private val _statistics = MutableStateFlow<UsersStatistics?>(null)
    val statistics: StateFlow<UsersStatistics?> = _statistics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isEmptyDatabase = MutableStateFlow(false)
    val isEmptyDatabase: StateFlow<Boolean> = _isEmptyDatabase.asStateFlow()

    fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isEmptyDatabase.value = false

            val result = getUsersStatisticsUseCase()
            if (result.isSuccess) {
                val stats = result.resultData
                _statistics.value = stats

                // Проверяем, пустая ли база данных
                if (stats?.totalUsers == 0) {
                    _isEmptyDatabase.value = true
                }
            } else {
                _error.value = result.message ?: "Failed to load statistics"
            }

            _isLoading.value = false
        }
    }

    fun refreshStatistics() {
        loadStatistics()
    }
}