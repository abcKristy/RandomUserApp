package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersWithFiltersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        count: Int = 1,
        gender: String? = null,
        nationality: String? = null
    ): Result<List<User>> {
        return repository.getUsersWithFilters(count, gender, nationality)
    }
}