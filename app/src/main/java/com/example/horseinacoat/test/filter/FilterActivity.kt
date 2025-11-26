package com.example.horseinacoat.test.filter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorseInACoatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FilterScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val selectedNationality by viewModel.selectedNationality.collectAsState()
    val nationalities by viewModel.nationalities.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Поиск пользователей",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Фильтры
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Фильтры поиска",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Выбор пола
                Text(
                    text = "Пол:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var genderExpanded by remember { mutableStateOf(false) }
                val genderOptions = listOf(
                    "Любой" to null,
                    "Мужской" to "male",
                    "Женский" to "female"
                )

                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    TextField(
                        value = genderOptions.find { it.second == selectedGender }?.first ?: "Любой",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { (label, value) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    viewModel.selectGender(value)
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Выбор национальности
                Text(
                    text = "Национальность:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var nationalityExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = nationalityExpanded,
                    onExpandedChange = { nationalityExpanded = !nationalityExpanded }
                ) {
                    TextField(
                        value = nationalities.find { it.code == selectedNationality }?.name ?: "Любая",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nationalityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = nationalityExpanded,
                        onDismissRequest = { nationalityExpanded = false }
                    ) {
                        // Опция "Любая"
                        DropdownMenuItem(
                            text = { Text("Любая") },
                            onClick = {
                                viewModel.selectNationality(null)
                                nationalityExpanded = false
                            }
                        )
                        // Все доступные национальности
                        nationalities.forEach { nationality ->
                            DropdownMenuItem(
                                text = { Text(nationality.name) },
                                onClick = {
                                    viewModel.selectNationality(nationality.code)
                                    nationalityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка поиска
                Button(
                    onClick = { viewModel.searchUser() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !userState.isLoading
                ) {
                    if (userState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Поиск...")
                    } else {
                        Text("Найти пользователя")
                    }
                }
            }
        }

        // Найденный пользователь
        if (userState.user != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Найденный пользователь:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    UserCard(user = userState.user!!)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveCurrentUser() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Сохранить в БД")
                        }
                        Button(
                            onClick = { viewModel.clearCurrentUser() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Очистить")
                        }
                    }
                }
            }
        }

        if (userState.error.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Ошибка: ${userState.error}",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.loadSavedUsers() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text("Показать сохраненных пользователей")
        }

        if (usersState.users.isNotEmpty()) {
            Text(
                text = "Сохраненные пользователи (${usersState.users.size}):",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usersState.users) { user ->
                    UserCard(user = user)
                }
            }
        } else if (usersState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${user.name.title} ${user.name.first} ${user.name.last}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (user.gender) {
                        "male" -> "♂️"
                        "female" -> "♀️"
                        else -> "⚧"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "📧 ${user.email}")
            Text(text = "📱 ${user.phone}")
            Text(text = "📍 ${user.location.street.name} ${user.location.street.number}, ${user.location.city}")
            Text(text = "🏴 ${user.location.country} (${user.nat})")

            if (user.isSaved) {
                Text(
                    text = "✅ Сохранен в БД",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}