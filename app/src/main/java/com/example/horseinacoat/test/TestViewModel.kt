package com.example.horseinacoat.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.*
import com.example.horseinacoat.test.filter.UserState
import com.example.horseinacoat.test.filter.UsersState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val saveUserUseCase: SaveUserUseCase,
    private val isUserSavedUseCase: IsUserSavedUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _usersState = MutableStateFlow(UsersState())
    val usersState: StateFlow<UsersState> = _usersState.asStateFlow()

    private val _testResults = MutableStateFlow<List<String>>(emptyList())
    val testResults: StateFlow<List<String>> = _testResults.asStateFlow()

    fun testGetRandomUser() {
        viewModelScope.launch {
            _userState.value = _userState.value.copy(isLoading = true, error = "")
            _testResults.value = _testResults.value + "Тест API: Запрос случайного пользователя..."

            val result = getRandomUserUseCase()
            when {
                result.isSuccess -> {
                    _userState.value = _userState.value.copy(
                        user = result.resultData,
                        isLoading = false
                    )
                    _testResults.value = _testResults.value + "Тест API: Успешно! Пользователь получен"
                }
                result.isError -> {
                    _userState.value = _userState.value.copy(
                        error = result.resultException?.message ?: "Unknown error",
                        isLoading = false
                    )
                    _testResults.value = _testResults.value + "ест API: Ошибка - ${result.resultException?.message}"
                }
            }
        }
    }

    fun testSaveUser() {
        viewModelScope.launch {
            val currentUser = _userState.value.user
            if (currentUser != null) {
                _testResults.value = _testResults.value + "Тест сохранения: Сохранение пользователя..."

                val result = saveUserUseCase(currentUser)
                when {
                    result.isSuccess -> {
                        _testResults.value = _testResults.value + "Тест сохранения: Пользователь сохранен в БД"

                        val isSaved = isUserSavedUseCase(currentUser.id)
                        if (isSaved) {
                            _testResults.value = _testResults.value + "Тест проверки: Пользователь найден в БД"
                        } else {
                            _testResults.value = _testResults.value + "Тест проверки: Пользователь не найден в БД"
                        }
                    }
                    result.isError -> {
                        _testResults.value = _testResults.value + "Тест сохранения: Ошибка - ${result.resultException?.message}"
                    }
                }
            } else {
                _testResults.value = _testResults.value + "Тест сохранения: Сначала получите пользователя"
            }
        }
    }

    fun testGetAllUsers() {
        viewModelScope.launch {
            _testResults.value = _testResults.value + "Тест БД: Загрузка всех пользователей..."

            val result = getAllUsersUseCase()
            when {
                result.isSuccess -> {
                    _usersState.value = _usersState.value.copy(users = result.resultData ?: emptyList())
                    _testResults.value = _testResults.value + "Тест БД: Загружено ${result.resultData?.size ?: 0} пользователей"
                }
                result.isError -> {
                    _testResults.value = _testResults.value + "Тест БД: Ошибка - ${result.resultException?.message}"
                }
            }
        }
    }

    fun runAllTests() {
        viewModelScope.launch {
            _testResults.value = emptyList()
            _testResults.value = _testResults.value + "Запуск всех тестов..."

            testGetRandomUser()
            kotlinx.coroutines.delay(1000)
            testSaveUser()
            kotlinx.coroutines.delay(500)
            testGetAllUsers()

            _testResults.value = _testResults.value + "Все тесты завершены"
        }
    }
}

data class UserState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String = ""
)

data class UsersState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)