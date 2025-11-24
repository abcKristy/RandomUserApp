package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.User
import com.example.horseinacoat.domain.repository.UserRepository

class SaveUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return repository.saveUser(user)
    }
}