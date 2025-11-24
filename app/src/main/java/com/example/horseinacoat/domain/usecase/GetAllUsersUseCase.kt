package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.User
import com.example.horseinacoat.domain.repository.UserRepository

class GetAllUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return repository.getAllUsers()
    }
}