package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUsersPaginatedUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersPaginatedUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUsersPaginatedUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetUsersPaginatedUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnPaginatedUsers() = runTest {
        // Given
        val page = 1
        val pageSize = 10
        val expectedUsers = TestData.testUserList
        coEvery { repository.getUsersPaginated(page, pageSize) } returns Result.Success(expectedUsers)

        // When
        val result = useCase(page, pageSize)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedUsers, (result as Result.Success).data)
    }

    @Test
    fun invoke_shouldHandleEmptyPage() = runTest {
        // Given
        val page = 5
        val pageSize = 10
        coEvery { repository.getUsersPaginated(page, pageSize) } returns Result.Success(emptyList())

        // When
        val result = useCase(page, pageSize)

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val page = 1
        val pageSize = 10
        val expectedException = Exception("Database error")
        coEvery { repository.getUsersPaginated(page, pageSize) } returns Result.Error(expectedException)

        // When
        val result = useCase(page, pageSize)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }
}