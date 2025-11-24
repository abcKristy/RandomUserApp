package com.example.horseinacoat.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.horseinacoat.test.TestActivity
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorseInACoatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchScreen(
                        onLaunchTest = {
                            startActivity(Intent(this, TestActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LaunchScreen(onLaunchTest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HorseInACoat",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Тестирование промежуточных результатов",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onLaunchTest,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Запустить тесты")
        }
    }
}