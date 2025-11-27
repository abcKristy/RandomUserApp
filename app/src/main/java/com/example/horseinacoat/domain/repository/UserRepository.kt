package com.example.horseinacoat.domain.repository

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.model.UsersStatistics

interface UserRepository {

    suspend fun getRandomUser(
        gender: String? = null,
        nationality: String? = null
    ): Result<User>

    suspend fun getAllUsers(): Result<List<User>>

    suspend fun saveUser(user: User): Result<Unit>

    suspend fun deleteUser(userId: String): com.example.horseinacoat.domain.model.Result<Unit>

    suspend fun getUserById(userId: String): com.example.horseinacoat.domain.model.Result<User>

    suspend fun isUserSaved(userId: String): Boolean

    suspend fun getUsersPaginated(
        page: Int,
        pageSize: Int
    ): Result<List<User>>

    suspend fun getUsersWithFilters(
        count: Int = 1,
        gender: String? = null,
        nationality: String? = null
    ): Result<List<User>>

    suspend fun getUsersStatistics(): Result<UsersStatistics>
}