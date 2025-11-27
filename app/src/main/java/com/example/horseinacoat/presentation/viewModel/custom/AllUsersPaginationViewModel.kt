package com.example.horseinacoat.presentation.viewModel.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.DeleteUserUseCase
import com.example.horseinacoat.domain.usecase.GetUsersPaginatedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllUsersPaginationViewModel @Inject constructor(
    private val getUsersPaginatedUseCase: GetUsersPaginatedUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow<User?>(null)
    val showDeleteDialog: StateFlow<User?> = _showDeleteDialog.asStateFlow()

    private val _deleteInProgress = MutableStateFlow<String?>(null)
    val deleteInProgress: StateFlow<String?> = _deleteInProgress.asStateFlow()

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getUsersPaginatedUseCase(
                page = 0,
                pageSize = _paginationState.value.pageSize
            )

            if (result.isSuccess) {
                val newUsers = result.resultData ?: emptyList()
                _users.value = newUsers

                _paginationState.update { state ->
                    state.copy(
                        currentPage = 0,
                        isLastPage = newUsers.size < state.pageSize,
                        totalItems = newUsers.size,
                        isLoading = false
                    )
                }
            } else {
                _error.value = result.message ?: "Failed to load users"
            }

            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        if (_paginationState.value.canLoadMore && !_isLoading.value) {
            viewModelScope.launch {
                _paginationState.update { it.copy(isLoading = true) }

                val nextPage = _paginationState.value.currentPage + 1
                val result = getUsersPaginatedUseCase(
                    page = nextPage,
                    pageSize = _paginationState.value.pageSize
                )

                if (result.isSuccess) {
                    val newUsers = result.resultData ?: emptyList()
                    val currentUsers = _users.value.toMutableList()
                    currentUsers.addAll(newUsers)

                    _users.value = currentUsers

                    _paginationState.update { state ->
                        state.copy(
                            currentPage = nextPage,
                            isLastPage = newUsers.isEmpty() || newUsers.size < state.pageSize,
                            totalItems = currentUsers.size,
                            isLoading = false
                        )
                    }
                } else {
                    _error.value = result.message ?: "Failed to load more users"
                    _paginationState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun showDeleteConfirmation(user: User) {
        _showDeleteDialog.value = user
    }

    fun hideDeleteConfirmation() {
        _showDeleteDialog.value = null
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _deleteInProgress.value = userId
            _error.value = null

            val result = deleteUserUseCase(userId)

            if (result.isSuccess) {
                val currentUsers = _users.value.toMutableList()
                currentUsers.removeAll { it.id == userId }
                _users.value = currentUsers

                _paginationState.update { state ->
                    state.copy(totalItems = currentUsers.size)
                }
                _showDeleteDialog.value = null
            } else {
                _error.value = result.message ?: "Failed to delete user"
            }

            _deleteInProgress.value = null
        }
    }

    fun refresh() {
        loadFirstPage()
    }

    fun clearError() {
        _error.value = null
    }
}