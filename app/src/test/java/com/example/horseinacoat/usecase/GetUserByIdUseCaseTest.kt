package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUserByIdUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserByIdUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetUserByIdUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnSuccess_whenUserExists() = runTest {
        // Given
        val userId = "1"
        val expectedUser = TestData.testUser
        coEvery { repository.getUserById(userId) } returns Result.Success(expectedUser)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedUser, (result as Result.Success).data)
    }

    @Test
    fun invoke_shouldReturnError_whenUserNotFound() = runTest {
        // Given
        val userId = "999"
        coEvery { repository.getUserById(userId) } returns Result.Error(Exception("User not found"))

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("User not found", (result as Result.Error).exception.message)
    }

    @Test
    fun invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val userId = "1"
        val expectedException = Exception("Database error")
        coEvery { repository.getUserById(userId) } returns Result.Error(expectedException)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }
}