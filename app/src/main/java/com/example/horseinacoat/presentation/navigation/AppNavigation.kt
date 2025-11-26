package com.example.horseinacoat.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.horseinacoat.presentation.screens.MainScreen
import com.example.horseinacoat.presentation.screens.custom.CustomMainScreen
import com.example.horseinacoat.presentation.screens.usual.ListRandomUserScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.MAIN_SCREEN
    ) {
        composable(NavigationRoutes.MAIN_SCREEN) {
            MainScreen(
                onRandomUserClick = {
                    navController.navigate(NavigationRoutes.LIST_RANDOM_USER_SCREEN)
                },
                onCustomUserClick = {
                    navController.navigate(NavigationRoutes.CUSTOM_MAIN_SCREEN)
                }
            )
        }

        composable(NavigationRoutes.CUSTOM_MAIN_SCREEN) {
            CustomMainScreen(navController = navController)
        }

        composable(NavigationRoutes.LIST_RANDOM_USER_SCREEN) {
            ListRandomUserScreen(navController = navController)
        }
    }
}