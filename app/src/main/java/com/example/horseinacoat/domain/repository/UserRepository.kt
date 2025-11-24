package com.example.horseinacoat.domain.repository

import com.example.horseinacoat.domain.User

interface UserRepository {

    suspend fun getRandomUser(
        gender: String? = null,
        nationality: String? = null
    ): Result<User>

    suspend fun getAllUsers(): Result<List<User>>

    suspend fun saveUser(user: User): Result<Unit>

    suspend fun deleteUser(userId: String): Result<Unit>

    suspend fun getUserById(userId: String): Result<User>

    suspend fun isUserSaved(userId: String): Boolean
}