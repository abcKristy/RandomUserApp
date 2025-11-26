package com.example.horseinacoat.presentation.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import com.example.horseinacoat.presentation.screens.usual.AddNewRandomUserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewRandomUserViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<AddNewRandomUserState>(AddNewRandomUserState.Initial)
    val userState: StateFlow<AddNewRandomUserState> = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun findRandomUser(gender: String?, nationality: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getRandomUserUseCase(
                gender = gender,
                nationality = nationality
            )

            if (result.isSuccess) {
                val user = result.resultData
                if (user != null) {
                    _userState.value = AddNewRandomUserState.UserFound(user)
                } else {
                    _error.value = "Пользователь не найден"
                }
            } else {
                _error.value = result.message ?: "Ошибка при поиске пользователя"
            }

            _isLoading.value = false
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = saveUserUseCase(user)

            if (result.isSuccess) {
                _userState.value = AddNewRandomUserState.UserSaved(user)
            } else {
                _error.value = result.message ?: "Ошибка при сохранении пользователя"
            }

            _isLoading.value = false
        }
    }
}