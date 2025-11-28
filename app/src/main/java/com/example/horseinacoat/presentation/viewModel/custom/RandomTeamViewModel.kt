package com.example.horseinacoat.presentation.viewModel.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.GetUsersWithFiltersUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomTeamViewModel @Inject constructor(
    private val getUsersWithFiltersUseCase: GetUsersWithFiltersUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _teamUsers = MutableStateFlow<List<User>>(emptyList())
    val teamUsers: StateFlow<List<User>> = _teamUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _selectedGender = MutableStateFlow<String?>(null)
    val selectedGender: StateFlow<String?> = _selectedGender.asStateFlow()

    private val _selectedNationality = MutableStateFlow<String?>(null)
    val selectedNationality: StateFlow<String?> = _selectedNationality.asStateFlow()

    private val _teamSize = MutableStateFlow(5)
    val teamSize: StateFlow<Int> = _teamSize.asStateFlow()

    fun generateRandomTeam() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _saveSuccess.value = false

            val result = getUsersWithFiltersUseCase(
                count = _teamSize.value,
                gender = _selectedGender.value,
                nationality = _selectedNationality.value
            )

            if (result.isSuccess) {
                val users = result.resultData ?: emptyList()
                _teamUsers.value = users
                if (users.isEmpty()) {
                    _error.value = "Не удалось найти пользователей с выбранными параметрами"
                }
            } else {
                _error.value = result.message ?: "Ошибка при генерации команды"
                _teamUsers.value = emptyList()
            }

            _isLoading.value = false
        }
    }

    fun saveTeamToDatabase() {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            _saveSuccess.value = false

            val users = _teamUsers.value
            if (users.isEmpty()) {
                _error.value = "Нет пользователей для сохранения"
                _isSaving.value = false
                return@launch
            }

            var successCount = 0
            var errorCount = 0

            users.forEach { user ->
                val result = saveUserUseCase(user)
                if (result.isSuccess) {
                    successCount++
                } else {
                    errorCount++
                }
            }

            if (errorCount > 0) {
                _error.value = "Сохранено $successCount из ${users.size} пользователей. Ошибок: $errorCount"
            } else {
                _saveSuccess.value = true
            }

            _isSaving.value = false
        }
    }

    fun setGender(gender: String?) {
        _selectedGender.value = gender
    }

    fun setNationality(nationality: String?) {
        _selectedNationality.value = nationality
    }

    fun setTeamSize(size: Int) {
        _teamSize.value = size
    }

    fun clearTeam() {
        _teamUsers.value = emptyList()
        _error.value = null
        _saveSuccess.value = false
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}