package com.example.horseinacoat.presentation.navigation

object NavigationRoutes {
    const val MAIN_SCREEN = "main_screen"
    const val CUSTOM_MAIN_SCREEN = "custom_main_screen"
    const val LIST_RANDOM_USER_SCREEN = "list_random_user_screen"
    const val ADD_NEW_RANDOM_USER_SCREEN = "add_new_random_user_screen"
    const val USER_DETAIL_SCREEN = "user_detail_screen"

    // Custom routes
    const val CUSTOM_LIST_RANDOM_USER_SCREEN = "custom_list_random_user_screen"
    const val CUSTOM_ADD_NEW_RANDOM_USER_SCREEN = "custom_add_new_random_user_screen"
    const val CUSTOM_USER_DETAIL_SCREEN = "custom_user_detail_screen"

    fun createUserDetailRoute(userId: String): String {
        return "user_detail_screen/$userId"
    }

    fun createCustomUserDetailRoute(userId: String): String {
        return "custom_user_detail_screen/$userId"
    }
}