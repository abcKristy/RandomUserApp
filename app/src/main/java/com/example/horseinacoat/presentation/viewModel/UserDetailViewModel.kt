package com.example.horseinacoat.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.DeleteUserUseCase
import com.example.horseinacoat.domain.usecase.GetUserByIdUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import com.example.horseinacoat.domain.usecase.IsUserSavedUseCase
import com.example.horseinacoat.presentation.viewModel.UserDetailUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val saveUser: SaveUserUseCase,
    private val deleteUser: DeleteUserUseCase,
    private val isUserSaved: IsUserSavedUseCase,
    private val getUserById: GetUserByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = UserDetailUiState.Loading
            try {
                val result = getUserById(userId)
                when (result) {
                    is Result.Success -> {
                        val isSaved = isUserSaved(userId)
                        _uiState.value = Success(
                            user = result.data.copy(isSaved = isSaved)
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = Error(
                            message = result.exception.message ?: "Failed to load user"
                        )
                    }

                    Result.Loading -> TODO()
                }
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error("Error loading user: ${e.message}")
            }
        }
    }

    fun setUser(user: User) {
        viewModelScope.launch {
            val isSaved = isUserSaved(user.id)
            _uiState.value = UserDetailUiState.Success(user.copy(isSaved = isSaved))
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            try {
                val result = saveUser(user)
                when (result) {
                    is Result.Success<*> -> {
                        _saveState.value = SaveState.Saved
                        // Обновляем состояние пользователя
                        if (_uiState.value is UserDetailUiState.Success) {
                            _uiState.value = UserDetailUiState.Success(user.copy(isSaved = true))
                        }
                    }
                    is Result.Error -> {
                        _saveState.value = SaveState.Error(
                            message = result.exception.message ?: "Failed to save user"
                        )
                    }
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Save error: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _saveState.value = SaveState.Deleting
            try {
                val result = deleteUser(userId)
                when (result) {
                    is Result.Success<*> -> {
                        _saveState.value = SaveState.Deleted
                        if (_uiState.value is UserDetailUiState.Success) {
                            val currentUser = (_uiState.value as UserDetailUiState.Success).user
                            _uiState.value = UserDetailUiState.Success(currentUser.copy(isSaved = false))
                        }
                    }
                    is com.example.horseinacoat.domain.model.Result.Error -> {
                        _saveState.value = SaveState.Error(
                            message = result.message ?: "Failed to delete user"
                        )
                    }
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Delete error: ${e.message}")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: User) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}

sealed class SaveState {
    object Idle : SaveState()
    object Saving : SaveState()
    object Saved : SaveState()
    object Deleting : SaveState()
    object Deleted : SaveState()
    data class Error(val message: String) : SaveState()
}