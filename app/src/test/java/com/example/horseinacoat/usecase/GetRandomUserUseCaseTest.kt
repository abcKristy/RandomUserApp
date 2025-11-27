package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRandomUserUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetRandomUserUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetRandomUserUseCase(repository)
    }

    @Test
    fun invoke_shouldReturnSuccess_whenRepositoryReturnsUser() = runTest {
        // Given
        val expectedUser = TestData.testUser
        coEvery { repository.getRandomUser(any(), any()) } returns Result.Success(expectedUser)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedUser, (result as Result.Success).data)
    }

    @Test
    fun invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val expectedException = Exception("Network error")
        coEvery { repository.getRandomUser(any(), any()) } returns Result.Error(expectedException)

        // When
        val result = useCase()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }

    @Test
    fun invoke_shouldPassCorrectParametersToRepository() = runTest {
        // Given
        val gender = "male"
        val nationality = "US"
        coEvery { repository.getRandomUser(gender, nationality) } returns Result.Success(TestData.testUser)

        // When
        useCase(gender, nationality)

        // Then
        coEvery { repository.getRandomUser(gender, nationality) }
    }

    @Test
    fun invoke_shouldUseDefaultParameters_whenNoParametersProvided() = runTest {
        // Given
        coEvery { repository.getRandomUser(null, null) } returns Result.Success(TestData.testUser)

        // When
        useCase()

        // Then
        coEvery { repository.getRandomUser(null, null) }
    }
}