package com.example.horseinacoat.presentation.navigation

object NavigationRoutes {
    const val MAIN_SCREEN = "main_screen"
    const val CUSTOM_MAIN_SCREEN = "custom_main_screen"
    const val LIST_RANDOM_USER_SCREEN = "list_random_user_screen"
    const val ADD_NEW_RANDOM_USER_SCREEN = "add_new_random_user_screen"
    const val CUSTOM_ADD_NEW_RANDOM_USER_SCREEN = "custom_add_new_random_user_screen"
    const val ALL_USERS_SCREEN = "all_users_screen"
    const val LOCATION_MATCH_SCREEN = "location_match_screen"
    const val RANDOM_TEAM_SCREEN = "random_team_screen"
    const val STATISTICS_SCREEN = "statistics_screen"
    const val RESUME_SCREEN = "resume_screen"

    fun createUserDetailRoute(userId: String): String {
        return "user_detail_screen/$userId"
    }

}