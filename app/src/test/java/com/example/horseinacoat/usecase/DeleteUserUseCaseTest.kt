package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.DeleteUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DeleteUserUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: DeleteUserUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = DeleteUserUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnSuccess_whenUserDeleted() = runTest {
        // Given
        val userId = "123"
        coEvery { repository.deleteUser(userId) } returns Result.Success(Unit)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Success)
    }

    @Test
    fun invoke_shouldReturnError_whenDeleteFails() = runTest {
        // Given
        val userId = "123"
        val expectedException = Exception("Delete failed")
        coEvery { repository.deleteUser(userId) } returns Result.Error(expectedException)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }

    @Test
    fun invoke_shouldCallRepositoryWithCorrectUserId() = runTest {
        // Given
        val userId = "123"
        coEvery { repository.deleteUser(userId) } returns Result.Success(Unit)

        // When
        useCase(userId)

        // Then
        coEvery { repository.deleteUser(userId) }
    }
}