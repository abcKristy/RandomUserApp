package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.DeleteUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Тестирует удаление пользователя: проверяет успешное удаление и обработку ошибок.
// Убеждается, что Use Case вызывает репозиторий с правильным ID и возвращает корректный Result.

class DeleteUserUseCaseTest {

    // Мокируем зависимости
    private lateinit var mockRepository: UserRepository
    private lateinit var deleteUserUseCase: DeleteUserUseCase

    @Before
    fun setUp() {
        // Создаем мок репозитория перед каждым тестом
        mockRepository = mockk(relaxed = true)
        deleteUserUseCase = DeleteUserUseCase(mockRepository)
    }

    @Test
    fun deleteUserUseCase_invoke_shouldReturnSuccess_whenRepositoryDeletesUser() = runTest {
        // Given (Подготовка)
        val testUserId = "user_123"
        coEvery { mockRepository.deleteUser(testUserId) } returns Result.Success(Unit)

        // When (Выполнение)
        val result = deleteUserUseCase(testUserId)

        // Then (Проверка)
        // 1. Проверяем что результат успешный
        assertTrue("Result should be Success", result is Result.Success)

        // 2. Проверяем что метод репозитория был вызван с правильным userId
        coVerify(exactly = 1) { mockRepository.deleteUser(testUserId) }

        // 3. Проверяем что результат содержит Unit (успешное выполнение)
        assertEquals(Unit, (result as Result.Success).data)
    }

    @Test
    fun deleteUserUseCase_invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given
        val testUserId = "user_123"
        val expectedErrorMessage = "Failed to delete user: Database error"
        val expectedException = Exception(expectedErrorMessage)

        coEvery { mockRepository.deleteUser(testUserId) } returns Result.Error(expectedException)

        // When
        val result = deleteUserUseCase(testUserId)

        // Then
        // 1. Проверяем что результат содержит ошибку
        assertTrue("Result should be Error", result is Result.Error)

        // 2. Проверяем что метод репозитория был вызван
        coVerify(exactly = 1) { mockRepository.deleteUser(testUserId) }

        // 3. Проверяем сообщение об ошибке
        assertEquals(expectedErrorMessage, (result as Result.Error).exception.message)
        assertEquals(expectedException, (result).exception)
    }

    @Test
    fun deleteUserUseCase_invoke_shouldPropagateRepositoryError() = runTest {
        // Given
        val testUserId = "user_456"
        val databaseException = RuntimeException("Database connection failed")
        coEvery { mockRepository.deleteUser(testUserId) } returns Result.Error(databaseException)

        // When
        val result = deleteUserUseCase(testUserId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(databaseException, (result as Result.Error).exception)
    }

    @Test
    fun deleteUserUseCase_invoke_shouldCallRepositoryExactlyOnce() = runTest {
        // Given
        val testUserId = "user_789"
        coEvery { mockRepository.deleteUser(testUserId) } returns Result.Success(Unit)

        // When
        deleteUserUseCase(testUserId)

        // Then
        // Проверяем что метод был вызван ровно один раз с правильными параметрами
        coVerify(exactly = 1) { mockRepository.deleteUser(testUserId) }
        coVerify(exactly = 0) { mockRepository.getAllUsers() } // Другие методы не должны вызываться
    }

    @Test
    fun deleteUserUseCase_constructor_shouldInjectRepository() {
        // Given
        val repository = mockk<UserRepository>()

        // When
        val useCase = DeleteUserUseCase(repository)

        // Then
        // Простая проверка что useCase создан (в реальности это проверяет DI)
        assertTrue(useCase is DeleteUserUseCase)
    }
}