package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUsersStatisticsUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersStatisticsUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUsersStatisticsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetUsersStatisticsUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnStatistics() = runTest {
        // Given
        val expectedStatistics = TestData.testStatistics
        coEvery { repository.getUsersStatistics() } returns Result.Success(expectedStatistics)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedStatistics, (result as Result.Success).data)
    }

    @Test
    fun invoke_shouldReturnEmptyStatistics_whenNoUsers() = runTest {
        // Given
        val emptyStatistics = TestData.emptyStatistics
        coEvery { repository.getUsersStatistics() } returns Result.Success(emptyStatistics)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.totalUsers)
        assertTrue((result).data.genderDistribution.isEmpty())
    }

    @Test
    fun invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val expectedException = Exception("Statistics calculation failed")
        coEvery { repository.getUsersStatistics() } returns Result.Error(expectedException)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }
}