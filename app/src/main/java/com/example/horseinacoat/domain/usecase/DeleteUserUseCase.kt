package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository

class DeleteUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.deleteUser(userId)
    }
}
