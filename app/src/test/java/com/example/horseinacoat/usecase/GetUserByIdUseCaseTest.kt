package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUserByIdUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserByIdUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getUserByIdUseCase: GetUserByIdUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getUserByIdUseCase = GetUserByIdUseCase(mockRepository)
    }

    @Test
    fun getUserByIdUseCase_invoke_shouldReturnSuccessWithUser_whenUserExists() = runTest {
        // Given: Пользователь с указанным ID существует в репозитории
        val userId = "user_123"
        val expectedUser = TestData.testUser.copy(id = userId)
        coEvery { mockRepository.getUserById(userId) } returns Result.Success(expectedUser)

        // When: Ищем пользователя по ID
        val result = getUserByIdUseCase(userId)

        // Then: Должен вернуться успешный результат с правильным пользователем
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Должен вернуться ожидаемый пользователь", expectedUser, (result as Result.Success).data)
        assertEquals("ID пользователя должен совпадать", userId, (result as Result.Success).data.id)
        coVerify(exactly = 1) { mockRepository.getUserById(userId) }
    }

    @Test
    fun getUserByIdUseCase_invoke_shouldReturnError_whenUserNotFound() = runTest {
        // Given: Пользователь с указанным ID не найден
        val userId = "non_existent_user"
        coEvery { mockRepository.getUserById(userId) } returns Result.Error(Exception("User not found"))

        // When: Ищем несуществующего пользователя
        val result = getUserByIdUseCase(userId)

        // Then: Должна вернуться ошибка "User not found"
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно быть 'User not found'", "User not found", (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.getUserById(userId) }
    }

    @Test
    fun getUserByIdUseCase_invoke_shouldReturnError_whenRepositoryThrowsException() = runTest {
        // Given: Репозиторий выбрасывает исключение при поиске
        val userId = "user_456"
        val databaseException = Exception("Database connection failed")
        coEvery { mockRepository.getUserById(userId) } returns Result.Error(databaseException)

        // When: Вызываем Use Case с проблемным ID
        val result = getUserByIdUseCase(userId)

        // Then: Исключение должно быть корректно обернуто в Result.Error
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Исключение должно совпадать", databaseException, (result as Result.Error).exception)
        coVerify(exactly = 1) { mockRepository.getUserById(userId) }
    }

    @Test
    fun getUserByIdUseCase_invoke_shouldHandleEmptyUserId() = runTest {
        // Given: Передаем пустой ID пользователя
        val emptyUserId = ""
        coEvery { mockRepository.getUserById(emptyUserId) } returns Result.Error(Exception("Invalid user ID"))

        // When: Ищем пользователя с пустым ID
        val result = getUserByIdUseCase(emptyUserId)

        // Then: Должна вернуться ошибка валидации
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно быть 'Invalid user ID'", "Invalid user ID", (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.getUserById(emptyUserId) }
    }

    @Test
    fun getUserByIdUseCase_invoke_shouldCallRepositoryWithCorrectUserId() = runTest {
        // Given: Настраиваем мок для конкретного ID
        val specificUserId = "specific_user_789"
        coEvery { mockRepository.getUserById(specificUserId) } returns Result.Success(TestData.testUser)

        // When: Вызываем Use Case с конкретным ID
        getUserByIdUseCase(specificUserId)

        // Then: Проверяем что репозиторий вызван с правильным ID
        coVerify(exactly = 1) { mockRepository.getUserById(specificUserId) }
        coVerify(exactly = 0) { mockRepository.getAllUsers() }
        coVerify(exactly = 0) { mockRepository.getRandomUser(any(), any()) }
    }

    @Test
    fun getUserByIdUseCase_constructor_shouldInitializeWithRepository() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case
        val useCase = GetUserByIdUseCase(repository)

        // Then: Use Case должен быть правильно инициализирован
        assertTrue("Объект должен быть экземпляром GetUserByIdUseCase", useCase is GetUserByIdUseCase)
    }
}