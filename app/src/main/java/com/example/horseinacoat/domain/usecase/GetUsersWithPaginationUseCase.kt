package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersPaginatedUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        page: Int,
        pageSize: Int
    ): Result<List<User>> {
        return repository.getUsersPaginated(page, pageSize)
    }
}