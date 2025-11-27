package com.example.horseinacoat.domain.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.UsersStatistics
import com.example.horseinacoat.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersStatisticsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<UsersStatistics> {
        return repository.getUsersStatistics()
    }
}