package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.repository.UserRepository

class GetUserByIdUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return repository.getUserById(userId)
    }
}