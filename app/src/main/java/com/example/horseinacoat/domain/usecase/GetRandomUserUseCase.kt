package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.repository.UserRepository

class GetRandomUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        gender: String? = null,
        nationality: String? = null
    ): Result<User> {
        return repository.getRandomUser(gender, nationality)
    }
}