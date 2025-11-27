package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetAllUsersUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetAllUsersUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetAllUsersUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnSuccessWithUserList() = runTest {
        // Given
        val expectedUsers = TestData.testUserList
        coEvery { repository.getAllUsers() } returns Result.Success(expectedUsers)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedUsers, (result as Result.Success).data)
    }

    @Test
    fun invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val expectedException = Exception("Database error")
        coEvery { repository.getAllUsers() } returns Result.Error(expectedException)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }

    @Test
    fun invoke_shouldReturnEmptyList_whenNoUsers() = runTest {
        // Given
        coEvery { repository.getAllUsers() } returns Result.Success(emptyList())

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
}