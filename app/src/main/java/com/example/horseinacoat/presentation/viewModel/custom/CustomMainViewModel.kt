// CustomMainViewModel.kt
package com.example.horseinacoat.presentation.viewModel.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomMainViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Состояния для креативных функций
    private val _creativeMessage = MutableStateFlow<String?>(null)
    val creativeMessage: StateFlow<String?> = _creativeMessage.asStateFlow()

    init {
        loadAllUsers()
    }

    fun loadRandomUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _creativeMessage.value = null

            val result = getRandomUserUseCase()
            if (result.isSuccess) {
                _currentUser.value = result.resultData
                generateCreativeMessage(result.resultData)
            } else {
                _error.value = result.message ?: "Failed to load random user"
            }

            _isLoading.value = false
        }
    }

    fun saveCurrentUser() {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val result = saveUserUseCase(user)
                if (result.isSuccess) {
                    loadAllUsers() // Обновляем список всех пользователей
                    _creativeMessage.value = "User ${user.name.first} saved successfully! 🎉"
                } else {
                    _error.value = result.message ?: "Failed to save user"
                }
            }
        }
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            val result = getAllUsersUseCase()
            if (result.isSuccess) {
                _allUsers.value = result.resultData ?: emptyList()
            }
        }
    }

    // Креативные функции
    private fun generateCreativeMessage(user: User?) {
        if (user == null) return

        val messages = listOf(
            "🌟 Meet ${user.name.first}! Their journey begins here!",
            "🎯 ${user.name.first} from ${user.location.country} - ready for new connections!",
            "🔮 ${user.name.first}'s story unfolds - age ${user.dob?.age ?: "unknown"}, from ${user.location.city}!",
            "💫 Discovered ${user.name.first}! A new face in our community!",
            "🎨 ${user.name.first} brings unique energy from ${user.location.state ?: user.location.country}!"
        )
        _creativeMessage.value = messages.random()
    }

    fun findSoulmate() {
        val currentUser = _currentUser.value ?: return
        val allUsers = _allUsers.value

        if (allUsers.isEmpty()) {
            _creativeMessage.value = "No users in database to find a soulmate for ${currentUser.name.first} 😔"
            return
        }

        val soulmate = allUsers
            .filter { it.id != currentUser.id }
            .maxByOrNull { calculateCompatibilityScore(currentUser, it) }

        soulmate?.let {
            _creativeMessage.value = "💖 Soulmate found! ${currentUser.name.first} is highly compatible with ${it.name.first} from ${it.location.country}! Compatibility score: ${calculateCompatibilityScore(currentUser, it)}%"
        } ?: run {
            _creativeMessage.value = "No suitable soulmate found for ${currentUser.name.first}"
        }
    }

    fun generateTeam() {
        val allUsers = _allUsers.value
        if (allUsers.size < 3) {
            _creativeMessage.value = "Need at least 3 users in database to form a team! Currently have ${allUsers.size}"
            return
        }

        val team = allUsers.shuffled().take(3)
        val teamNames = team.joinToString(", ") { it.name.first }
        val countries = team.map { it.location.country }.distinct()
        val genders = team.map { it.gender }.distinct()

        _creativeMessage.value = "🚀 Dream Team Formed: $teamNames! " +
                "Representing ${countries.size} countries, " +
                "${genders.size} genders. " +
                "Perfect balance for any project! 🎯"
    }

    fun selectUserOfTheMonth() {
        val allUsers = _allUsers.value
        if (allUsers.isEmpty()) {
            _creativeMessage.value = "No users available for selection 📭"
            return
        }

        val userOfTheMonth = allUsers.random()
        _creativeMessage.value = "🏆 User of the Month: ${userOfTheMonth.name.first} ${userOfTheMonth.name.last}! " +
                "From ${userOfTheMonth.location.city}, ${userOfTheMonth.location.country}. " +
                "Congratulations! 🎉"
    }

    fun findLocationMatch() {
        val currentUser = _currentUser.value ?: return
        val allUsers = _allUsers.value

        val locationMatches = allUsers.filter {
            it.id != currentUser.id &&
                    it.location.country == currentUser.location.country
        }

        if (locationMatches.isNotEmpty()) {
            val match = locationMatches.random()
            _creativeMessage.value = "🗺️ Location match! ${currentUser.name.first} and ${match.name.first} both from ${currentUser.location.country}! Maybe neighbors? 🏠"
        } else {
            _creativeMessage.value = "No location matches found for ${currentUser.name.first} from ${currentUser.location.country}"
        }
    }

    fun clearMessages() {
        _creativeMessage.value = null
        _error.value = null
    }

    private fun calculateCompatibilityScore(user1: User, user2: User): Int {
        var score = 0

        // Совпадение по возрасту (±5 лет)
        val age1 = user1.dob?.age ?: 0
        val age2 = user2.dob?.age ?: 0
        if (age1 > 0 && age2 > 0 && Math.abs(age1 - age2) <= 5) score += 25

        // Совпадение по стране
        if (user1.location.country == user2.location.country) score += 30

        // Совпадение по полу
        if (user1.gender == user2.gender) score += 15

        // Совпадение по национальности
        if (user1.nat == user2.nat) score += 20

        // Совпадение по городу (бонус)
        if (user1.location.city == user2.location.city) score += 10

        return score.coerceAtMost(100)
    }

    // Новая функция: Анализ коллекции
    fun analyzeCollection() {
        val allUsers = _allUsers.value
        if (allUsers.isEmpty()) {
            _creativeMessage.value = "Collection is empty! Add some users first 📊"
            return
        }

        val countries = allUsers.map { it.location.country }.distinct().size
        val averageAge = allUsers.mapNotNull { it.dob?.age }.average().toInt()
        val genderDistribution = allUsers.groupBy { it.gender }.mapValues { it.value.size }

        _creativeMessage.value = "📊 Collection Analysis:\n" +
                "• Total users: ${allUsers.size}\n" +
                "• Countries: $countries\n" +
                "• Average age: $averageAge\n" +
                "• Gender distribution: ${genderDistribution.entries.joinToString(", ") { "${it.key}: ${it.value}" }}"
    }
}