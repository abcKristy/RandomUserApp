package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUsersWithFiltersUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersWithFiltersUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUsersWithFiltersUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetUsersWithFiltersUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnFilteredUsers() = runTest {
        // Given
        val count = 5
        val gender = "male"
        val nationality = "US"
        val expectedUsers = TestData.testUserList
        coEvery { repository.getUsersWithFilters(count, gender, nationality) } returns Result.Success(expectedUsers)

        // When
        val result = useCase(count, gender, nationality)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedUsers, (result as Result.Success).data)
    }

    @Test
    fun invoke_shouldUseDefaultParameters_whenNoParametersProvided() = runTest {
        // Given
        coEvery { repository.getUsersWithFilters(1, null, null) } returns Result.Success(emptyList())

        // When
        useCase()

        // Then
        coEvery { repository.getUsersWithFilters(1, null, null) }
    }

    @Test
    fun invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val expectedException = Exception("API error")
        coEvery { repository.getUsersWithFilters(any(), any(), any()) } returns Result.Error(expectedException)

        // When
        val result = useCase(5, "female", "FR")

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }
}