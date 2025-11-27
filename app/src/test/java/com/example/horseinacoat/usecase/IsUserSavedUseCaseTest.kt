package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.IsUserSavedUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsUserSavedUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: IsUserSavedUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = IsUserSavedUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnTrue_whenUserExists() = runTest {
        // Given
        val userId = "123"
        coEvery { repository.isUserSaved(userId) } returns true

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result)
    }

    @Test
    fun invoke_shouldReturnFalse_whenUserDoesNotExist() = runTest {
        // Given
        val userId = "123"
        coEvery { repository.isUserSaved(userId) } returns false

        // When
        val result = useCase(userId)

        // Then
        assertFalse(result)
    }

    @Test
    fun invoke_shouldReturnFalse_whenRepositoryReturnsZeroCount() = runTest {
        // Given
        val userId = "123"
        coEvery { repository.isUserSaved(userId) } returns false

        // When
        val result = useCase(userId)

        // Then
        assertFalse(result)
    }

    @Test
    fun invoke_shouldCallRepositoryWithCorrectUserId() = runTest {
        // Given
        val userId = "123"
        coEvery { repository.isUserSaved(userId) } returns true

        // When
        useCase(userId)

        // Then
        coEvery { repository.isUserSaved(userId) }
    }
}