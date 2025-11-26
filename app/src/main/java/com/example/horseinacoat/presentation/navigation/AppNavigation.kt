package com.example.horseinacoat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.horseinacoat.presentation.screens.MainScreen
import com.example.horseinacoat.presentation.screens.custom.CustomAddNewScreen
import com.example.horseinacoat.presentation.screens.custom.CustomListScreen
import com.example.horseinacoat.presentation.screens.custom.CustomMainScreen
import com.example.horseinacoat.presentation.screens.custom.CustomUserDetailScreen
import com.example.horseinacoat.presentation.screens.usual.AddNewRandomUserScreen
import com.example.horseinacoat.presentation.screens.usual.ListRandomUserScreen
import com.example.horseinacoat.presentation.screens.usual.UserDetailScreen

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

        // Usual screens
        composable(NavigationRoutes.LIST_RANDOM_USER_SCREEN) {
            ListRandomUserScreen(navController = navController)
        }

        composable(NavigationRoutes.ADD_NEW_RANDOM_USER_SCREEN) {
            AddNewRandomUserScreen(navController = navController)
        }

        composable(
            route = "user_detail_screen/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailScreen(
                navController = navController,
                userId = userId
            )
        }

        // Custom screens
        composable(NavigationRoutes.CUSTOM_LIST_RANDOM_USER_SCREEN) {
            CustomListScreen(navController = navController)
        }

        composable(NavigationRoutes.CUSTOM_ADD_NEW_RANDOM_USER_SCREEN) {
            CustomAddNewScreen(navController = navController)
        }

        composable(
            route = "custom_user_detail_screen/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            CustomUserDetailScreen(
                navController = navController,
                userId = userId
            )
        }
    }
}