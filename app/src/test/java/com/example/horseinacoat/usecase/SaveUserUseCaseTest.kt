package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SaveUserUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: SaveUserUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SaveUserUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnSuccess_whenUserSaved() = runTest {
        // Given
        val user = TestData.testUser
        coEvery { repository.saveUser(user) } returns Result.Success(Unit)

        // When
        val result = useCase(user)

        // Then
        assertTrue(result is Result.Success)
    }

    @Test
    fun invoke_shouldReturnError_whenSaveFails() = runTest {
        // Given
        val user = TestData.testUser
        val expectedException = Exception("Save failed")
        coEvery { repository.saveUser(user) } returns Result.Error(expectedException)

        // When
        val result = useCase(user)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }

    @Test
    fun invoke_shouldCallRepositoryWithCorrectUser() = runTest {
        // Given
        val user = TestData.testUser
        coEvery { repository.saveUser(user) } returns Result.Success(Unit)

        // When
        useCase(user)

        // Then
        coEvery { repository.saveUser(user) }
    }
}