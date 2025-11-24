package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.repository.UserRepository

class IsUserSavedUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Boolean {
        return repository.isUserSaved(userId)
    }
}