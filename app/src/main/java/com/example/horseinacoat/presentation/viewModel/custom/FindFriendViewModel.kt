package com.example.horseinacoat.presentation.viewModel.custom


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindFriendViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _savedUsers = MutableStateFlow<List<User>>(emptyList())
    val savedUsers: StateFlow<List<User>> = _savedUsers.asStateFlow()

    private val _foundFriend = MutableStateFlow<User?>(null)
    val foundFriend: StateFlow<User?> = _foundFriend.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    private val _friendSaved = MutableSharedFlow<Boolean>()
    val friendSaved = _friendSaved.asSharedFlow()

    init {
        loadSavedUsers()
    }

    fun loadSavedUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = getAllUsersUseCase()
            if (result.isSuccess) {
                _savedUsers.value = result.resultData ?: emptyList()
            } else {
                _error.value = result.message ?: "Ошибка при загрузке пользователей"
            }

            _isLoading.value = false
        }
    }

    fun selectUser(user: User) {
        _selectedUser.value = user
        _foundFriend.value = null
        _error.value = null
    }

    fun findFriendForSelectedUser() {
        viewModelScope.launch {
            val selectedUser = _selectedUser.value
            if (selectedUser == null) {
                _error.value = "Выберите пользователя из списка"
                return@launch
            }

            _isLoading.value = true
            _error.value = null

            val result = getRandomUserUseCase(
                gender = null,
                nationality = selectedUser.nat
            )

            if (result.isSuccess) {
                val friend = result.resultData
                if (friend != null) {
                    _foundFriend.value = friend
                } else {
                    _error.value = "Не удалось найти подходящего друга"
                }
            } else {
                _error.value = result.message ?: "Ошибка при поиске друга"
            }

            _isLoading.value = false
        }
    }

    fun saveFoundFriend() {
        viewModelScope.launch {
            val friend = _foundFriend.value
            if (friend == null) {
                _error.value = "Нет найденного пользователя для сохранения"
                return@launch
            }

            _isLoading.value = true
            _error.value = null

            val result = saveUserUseCase(friend)
            if (result.isSuccess) {
                _friendSaved.emit(true)
                loadSavedUsers()
            } else {
                _error.value = result.message ?: "Ошибка при сохранении друга"
            }

            _isLoading.value = false
        }
    }

    fun clearSelection() {
        _selectedUser.value = null
        _foundFriend.value = null
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}