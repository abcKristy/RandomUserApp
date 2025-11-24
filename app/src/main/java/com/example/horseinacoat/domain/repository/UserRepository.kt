package com.example.horseinacoat.domain.repository

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User

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
}