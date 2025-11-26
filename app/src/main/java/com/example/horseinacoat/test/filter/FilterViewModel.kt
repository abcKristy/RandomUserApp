package com.example.horseinacoat.test.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.data.remote.api.ApiConstants
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Nationality(
    val code: String,
    val name: String,
    val flag: String
)

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow(_root_ide_package_.com.example.horseinacoat.test.filter.UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _usersState = MutableStateFlow(_root_ide_package_.com.example.horseinacoat.test.filter.UsersState())
    val usersState: StateFlow<UsersState> = _usersState.asStateFlow()

    private val _selectedGender = MutableStateFlow<String?>(null)
    val selectedGender: StateFlow<String?> = _selectedGender.asStateFlow()

    private val _selectedNationality = MutableStateFlow<String?>(null)
    val selectedNationality: StateFlow<String?> = _selectedNationality.asStateFlow()

    private val _nationalities = MutableStateFlow<List<Nationality>>(emptyList())
    val nationalities: StateFlow<List<Nationality>> = _nationalities.asStateFlow()

    init {
        loadNationalities()
    }

    private fun loadNationalities() {
        val nationalityMap = mapOf(
            "AU" to "🇦🇺 Australia",
            "BR" to "🇧🇷 Brazil",
            "CA" to "🇨🇦 Canada",
            "CH" to "🇨🇭 Switzerland",
            "DE" to "🇩🇪 Germany",
            "DK" to "🇩🇰 Denmark",
            "ES" to "🇪🇸 Spain",
            "FI" to "🇫🇮 Finland",
            "FR" to "🇫🇷 France",
            "GB" to "🇬🇧 Great Britain",
            "IE" to "🇮🇪 Ireland",
            "IN" to "🇮🇳 India",
            "IR" to "🇮🇷 Iran",
            "MX" to "🇲🇽 Mexico",
            "NL" to "🇳🇱 Netherlands",
            "NO" to "🇳🇴 Norway",
            "NZ" to "🇳🇿 New Zealand",
            "RS" to "🇷🇸 Serbia",
            "TR" to "🇹🇷 Turkey",
            "UA" to "🇺🇦 Ukraine",
            "US" to "🇺🇸 USA"
        )

        val nationalitiesList = ApiConstants.SUPPORTED_NATIONALITIES.map { code ->
            Nationality(
                code = code,
                name = nationalityMap[code] ?: code,
                flag = nationalityMap[code]?.substring(0, 2) ?: "🏴"
            )
        }.sortedBy { it.name }

        _nationalities.value = nationalitiesList
    }

    fun selectGender(gender: String?) {
        _selectedGender.value = gender
    }

    fun selectNationality(nationality: String?) {
        _selectedNationality.value = nationality
    }

    fun searchUser() {
        viewModelScope.launch {
            _userState.value = _userState.value.copy(
                isLoading = true,
                error = ""
            )

            val result = getRandomUserUseCase(
                gender = _selectedGender.value,
                nationality = _selectedNationality.value
            )

            when {
                result.isSuccess -> {
                    _userState.value = _userState.value.copy(
                        user = result.resultData,
                        isLoading = false
                    )
                }
                result.isError -> {
                    _userState.value = _userState.value.copy(
                        error = result.resultException?.message ?: "Unknown error",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveCurrentUser() {
        viewModelScope.launch {
            val currentUser = _userState.value.user
            if (currentUser != null) {
                saveUserUseCase(currentUser)
                // Обновляем список после сохранения
                loadSavedUsers()
            }
        }
    }

    fun clearCurrentUser() {
        _userState.value = _userState.value.copy(user = null)
    }

    fun loadSavedUsers() {
        viewModelScope.launch {
            _usersState.value = _usersState.value.copy(isLoading = true)

            val result = getAllUsersUseCase()
            when {
                result.isSuccess -> {
                    _usersState.value = _usersState.value.copy(
                        users = result.resultData ?: emptyList(),
                        isLoading = false
                    )
                }
                result.isError -> {
                    _usersState.value = _usersState.value.copy(
                        error = result.resultException?.message ?: "Unknown error",
                        isLoading = false
                    )
                }
            }
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