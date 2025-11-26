package com.example.horseinacoat.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import com.example.horseinacoat.domain.usecase.IsUserSavedUseCase
import com.example.horseinacoat.presentation.viewModel.MainScreenUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getRandomUser: GetRandomUserUseCase,
    private val saveUser: SaveUserUseCase,
    private val isUserSaved: IsUserSavedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Idle)
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    private val _selectedGender = MutableStateFlow<String?>(null)
    val selectedGender: StateFlow<String?> = _selectedGender.asStateFlow()

    private val _selectedNationality = MutableStateFlow<String?>(null)
    val selectedNationality: StateFlow<String?> = _selectedNationality.asStateFlow()

    fun generateRandomUser() {
        viewModelScope.launch {
            _uiState.value = MainScreenUiState.Loading
            try {
                val result = getRandomUser(
                    gender = _selectedGender.value,
                    nationality = _selectedNationality.value
                )
                when (result) {
                    is Result.Success -> {
                        val user = result.data
                        val isSaved = isUserSaved(user.id)
                        _uiState.value = Success(
                            user = user.copy(isSaved = isSaved)
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = Error(result.message ?: "Unknown error")
                    }

                    Result.Loading -> TODO()
                }
            } catch (e: Exception) {
                _uiState.value = MainScreenUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun saveCurrentUser(user: User) {
        viewModelScope.launch {
            _uiState.value = MainScreenUiState.Loading
            try {
                val result = saveUser(user)
                when (result) {
                    is Result.Success -> {
                        // Обновляем состояние пользователя как сохраненного
                        if (_uiState.value is Success) {
                            val currentUser = (_uiState.value as Success).user
                            _uiState.value = Success(
                                user = currentUser.copy(isSaved = true)
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = Error("Failed to save user: ${result.message}")
                    }

                    Result.Loading -> TODO()
                }
            } catch (e: Exception) {
                _uiState.value = MainScreenUiState.Error("Save error: ${e.message}")
            }
        }
    }

    fun setGender(gender: String?) {
        _selectedGender.value = gender
    }

    fun setNationality(nationality: String?) {
        _selectedNationality.value = nationality
    }

    fun resetState() {
        _uiState.value = MainScreenUiState.Idle
    }
}

sealed class MainScreenUiState {
    object Idle : MainScreenUiState()
    object Loading : MainScreenUiState()
    data class Success(val user: User) : MainScreenUiState()
    data class Error(val message: String) : MainScreenUiState()
}