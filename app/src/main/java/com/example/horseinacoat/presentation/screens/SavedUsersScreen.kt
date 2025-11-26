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
fun SavedUsersScreen(
    onBackClick: () -> Unit,
    onUserDetailClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сохраненные пользователи") }
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
            Text("Список сохраненных пользователей")

            Button(
                onClick = { onUserDetailClick("saved_user_1") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Перейти к пользователю")
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