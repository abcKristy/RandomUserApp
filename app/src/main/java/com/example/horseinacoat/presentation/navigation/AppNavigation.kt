package com.example.horseinacoat.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.horseinacoat.presentation.screens.CustomUserScreen
import com.example.horseinacoat.presentation.screens.MainScreen
import com.example.horseinacoat.presentation.screens.RandomUserScreen
import com.example.horseinacoat.presentation.screens.SavedUsersScreen
import com.example.horseinacoat.presentation.screens.UserDetailScreen

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
                    navController.navigate(NavigationRoutes.RANDOM_USER_SCREEN)
                },
                onCustomUserClick = {
                    navController.navigate(NavigationRoutes.CUSTOM_USER_SCREEN)
                }
            )
        }

        composable(NavigationRoutes.RANDOM_USER_SCREEN) {
            RandomUserScreen(
                onBackClick = { navController.popBackStack() },
                onUserDetailClick = { userId ->
                    navController.navigate("${NavigationRoutes.USER_DETAIL_SCREEN}/$userId")
                },
                onSavedUsersClick = {
                    navController.navigate(NavigationRoutes.SAVED_USERS_SCREEN)
                }
            )
        }

        composable(NavigationRoutes.CUSTOM_USER_SCREEN) {
            CustomUserScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("${NavigationRoutes.USER_DETAIL_SCREEN}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.SAVED_USERS_SCREEN) {
            SavedUsersScreen(
                onBackClick = { navController.popBackStack() },
                onUserDetailClick = { userId ->
                    navController.navigate("${NavigationRoutes.USER_DETAIL_SCREEN}/$userId")
                }
            )
        }
    }
}