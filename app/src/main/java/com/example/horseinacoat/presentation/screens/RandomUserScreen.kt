package com.example.horseinacoat.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomUserScreen(
    onBackClick: () -> Unit,
    onUserDetailClick: (String) -> Unit,
    onSavedUsersClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Обычный режим") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Экран обычного режима")

            Button(
                onClick = { onUserDetailClick("test_user_id") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Перейти к деталям пользователя")
            }

            Button(
                onClick = onSavedUsersClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Сохраненные пользователи")
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Назад")
            }
        }
    }
}