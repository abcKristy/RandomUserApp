package com.example.horseinacoat.presentation.screens.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.horseinacoat.ui.theme.HorseInACoatTheme

@Composable
fun RandomTeamScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Рандом команда",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
@Preview(name = "Random Team - Day", showBackground = true)
@Composable
fun RandomTeamScreenDayPreview() {
    HorseInACoatTheme(darkTheme = false) {
        RandomTeamScreen(navController = rememberNavController())
    }
}

@Preview(name = "Random Team - Night", showBackground = true)
@Composable
fun RandomTeamScreenNightPreview() {
    HorseInACoatTheme(darkTheme = true) {
        RandomTeamScreen(navController = rememberNavController())
    }
}