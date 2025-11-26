package com.example.horseinacoat.test

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.test.filter.UserCard
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorseInACoatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestScreen()
                }
            }
        }
    }
}

@Composable
fun TestScreen(
    viewModel: TestViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()
    val testResults by viewModel.testResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Тестирование приложения",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(onClick = { viewModel.testGetRandomUser() }) {
                Text("Тест API")
            }
            Button(onClick = { viewModel.testSaveUser() }) {
                Text("Тест Сохранения")
            }
            Button(onClick = { viewModel.testGetAllUsers() }) {
                Text("Тест БД")
            }
            Button(onClick = { viewModel.runAllTests() }) {
                Text("Все тесты")
            }
        }

        if (testResults.isNotEmpty()) {
            Text(
                text = "Результаты тестов:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(testResults) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(
                            text = result,
                            modifier = Modifier.padding(8.dp),
                            color = when {
                                result.contains("✅") -> MaterialTheme.colorScheme.primary
                                result.contains("❌") -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        when {
            userState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            userState.user != null -> {
                UserCard(user = userState.user!!)
            }
            userState.error.isNotEmpty() -> {
                Text(
                    text = "Ошибка: ${userState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (usersState.users.isNotEmpty()) {
            Text(
                text = "Сохраненные пользователи (${usersState.users.size}):",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(usersState.users) { user ->
                    UserCard(user = user)
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "${user.name.first} ${user.name.last}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Email: ${user.email}")
            Text(text = "Телефон: ${user.phone}")
            Text(text = "Страна: ${user.nat}")
            Text(text = "ID: ${user.id.take(8)}...")
        }
    }
}