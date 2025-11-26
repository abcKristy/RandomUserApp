package com.example.horseinacoat.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.DeleteUserUseCase
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.presentation.viewModel.SavedUsersUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedUsersViewModel @Inject constructor(
    private val getAllUsers: GetAllUsersUseCase,
    private val deleteUser: DeleteUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SavedUsersUiState>(SavedUsersUiState.Loading)
    val uiState: StateFlow<SavedUsersUiState> = _uiState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    init {
        loadSavedUsers()
    }

    fun loadSavedUsers() {
        viewModelScope.launch {
            _uiState.value = SavedUsersUiState.Loading
            try {
                val result = getAllUsers()
                when (result) {
                    is Result.Success -> {
                        val users = result.data
                        if (users.isEmpty()) {
                            _uiState.value = SavedUsersUiState.Empty
                        } else {
                            _uiState.value = Success(users)
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = Error(
                            message = result.exception.message ?: "Failed to load users"
                        )
                    }

                    Result.Loading -> TODO()
                }
            } catch (e: Exception) {
                _uiState.value = SavedUsersUiState.Error("Error loading users: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Deleting(userId)
            try {
                val result = deleteUser(userId)
                when (result) {
                    is Result.Success<*> -> {
                        _deleteState.value = DeleteState.Deleted(userId)
                        // Перезагружаем список
                        loadSavedUsers()
                    }
                    is Result.Error -> {
                        _deleteState.value = DeleteState.Error(
                            userId = userId,
                            message = result.exception.message ?: "Failed to delete user"
                        )
                    }
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(
                    userId = userId,
                    message = "Delete error: ${e.message}"
                )
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }
}

sealed class SavedUsersUiState {
    object Loading : SavedUsersUiState()
    object Empty : SavedUsersUiState()
    data class Success(val users: List<User>) : SavedUsersUiState()
    data class Error(val message: String) : SavedUsersUiState()
}

sealed class DeleteState {
    object Idle : DeleteState()
    data class Deleting(val userId: String) : DeleteState()
    data class Deleted(val userId: String) : DeleteState()
    data class Error(val userId: String, val message: String) : DeleteState()
}