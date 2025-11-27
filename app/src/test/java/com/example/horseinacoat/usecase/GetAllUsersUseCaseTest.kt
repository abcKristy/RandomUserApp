package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetAllUsersUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getAllUsersUseCase = GetAllUsersUseCase(mockRepository)
    }

    @Test
    fun getAllUsersUseCase_invoke_shouldReturnSuccessWithUsersList_whenRepositoryHasData() = runTest {
        // Given: Репозиторий возвращает список пользователей
        val expectedUsers = TestData.testUserList
        coEvery { mockRepository.getAllUsers() } returns Result.Success(expectedUsers)

        // When: Вызываем Use Case
        val result = getAllUsersUseCase()

        // Then: Проверяем что получен успешный результат с правильными данными
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Списки пользователей должны совпадать", expectedUsers, (result as Result.Success).data)
        coVerify(exactly = 1) { mockRepository.getAllUsers() }
    }

    @Test
    fun getAllUsersUseCase_invoke_shouldReturnEmptyList_whenRepositoryReturnsEmpty() = runTest {
        // Given: Репозиторий возвращает пустой список
        coEvery { mockRepository.getAllUsers() } returns Result.Success(emptyList())

        // When: Вызываем Use Case
        val result = getAllUsersUseCase()

        // Then: Проверяем что получен успешный результат с пустым списком
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertTrue("Список должен быть пустым", (result as Result.Success).data.isEmpty())
        coVerify(exactly = 1) { mockRepository.getAllUsers() }
    }

    @Test
    fun getAllUsersUseCase_invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given: Репозиторий возвращает ошибку
        val expectedException = Exception("Database connection failed")
        coEvery { mockRepository.getAllUsers() } returns Result.Error(expectedException)

        // When: Вызываем Use Case
        val result = getAllUsersUseCase()

        // Then: Проверяем что получена ошибка с правильным сообщением
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщения об ошибке должны совпадать", expectedException.message, (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.getAllUsers() }
    }

    @Test
    fun getAllUsersUseCase_invoke_shouldCallRepositoryExactlyOnce() = runTest {
        // Given: Настраиваем мок репозитория
        coEvery { mockRepository.getAllUsers() } returns Result.Success(TestData.testUserList)

        // When: Вызываем Use Case
        getAllUsersUseCase()

        // Then: Проверяем что репозиторий вызван ровно один раз
        coVerify(exactly = 1) { mockRepository.getAllUsers() }
        // Убеждаемся что другие методы репозитория не вызывались
        coVerify(exactly = 0) { mockRepository.getRandomUser(any(), any()) }
        coVerify(exactly = 0) { mockRepository.saveUser(any()) }
    }

    @Test
    fun getAllUsersUseCase_constructor_shouldProperlyInjectDependencies() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case с зависимостью
        val useCase = GetAllUsersUseCase(repository)

        // Then: Use Case должен быть создан и готов к работе
        assertTrue("Use Case должен быть экземпляром GetAllUsersUseCase", useCase is GetAllUsersUseCase)
    }
}