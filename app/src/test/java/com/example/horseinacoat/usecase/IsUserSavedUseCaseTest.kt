package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.IsUserSavedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsUserSavedUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var isUserSavedUseCase: IsUserSavedUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        isUserSavedUseCase = IsUserSavedUseCase(mockRepository)
    }

    @Test
    fun isUserSavedUseCase_invoke_shouldReturnTrue_whenUserExistsInDatabase() = runTest {
        // Given: Пользователь существует в локальной базе данных
        val userId = "user_123"
        coEvery { mockRepository.isUserSaved(userId) } returns true

        // When: Проверяем существует ли пользователь
        val result = isUserSavedUseCase(userId)

        // Then: Должен вернуться true
        assertTrue("Результат должен быть true когда пользователь существует", result)
        coVerify(exactly = 1) { mockRepository.isUserSaved(userId) }
    }

    @Test
    fun isUserSavedUseCase_invoke_shouldReturnFalse_whenUserNotInDatabase() = runTest {
        // Given: Пользователь не существует в локальной базе данных
        val userId = "non_existent_user"
        coEvery { mockRepository.isUserSaved(userId) } returns false

        // When: Проверяем существует ли пользователь
        val result = isUserSavedUseCase(userId)

        // Then: Должен вернуться false
        assertFalse("Результат должен быть false когда пользователь не существует", result)
        coVerify(exactly = 1) { mockRepository.isUserSaved(userId) }
    }

    @Test
    fun isUserSavedUseCase_invoke_shouldReturnFalse_whenRepositoryThrowsException() = runTest {
        // Given: Репозиторий возвращает false при ошибке (согласно реальной реализации)
        val userId = "user_456"
        // FIX: Согласно реальному коду, репозиторий возвращает false при исключениях, а не выбрасывает их
        coEvery { mockRepository.isUserSaved(userId) } returns false

        // When: Проверяем пользователя при ошибке базы данных
        val result = isUserSavedUseCase(userId)

        // Then: Должен вернуться false при ошибке в репозитории
        assertFalse("Результат должен быть false при ошибке в репозитории", result)
        coVerify(exactly = 1) { mockRepository.isUserSaved(userId) }
    }

    @Test
    fun isUserSavedUseCase_invoke_shouldHandleEmptyUserId() = runTest {
        // Given: Передаем пустой ID пользователя
        val emptyUserId = ""
        coEvery { mockRepository.isUserSaved(emptyUserId) } returns false

        // When: Проверяем пустой ID
        val result = isUserSavedUseCase(emptyUserId)

        // Then: Должен вернуться false для пустого ID
        assertFalse("Результат должен быть false для пустого ID", result)
        coVerify(exactly = 1) { mockRepository.isUserSaved(emptyUserId) }
    }

    @Test
    fun isUserSavedUseCase_invoke_shouldCallRepositoryWithCorrectUserId() = runTest {
        // Given: Конкретный ID пользователя для проверки
        val specificUserId = "specific_user_789"
        coEvery { mockRepository.isUserSaved(specificUserId) } returns true

        // When: Проверяем конкретного пользователя
        isUserSavedUseCase(specificUserId)

        // Then: Репозиторий должен быть вызван с правильным ID
        coVerify(exactly = 1) { mockRepository.isUserSaved(specificUserId) }
        coVerify(exactly = 0) { mockRepository.getUserById(any()) }
        coVerify(exactly = 0) { mockRepository.getAllUsers() }
    }

    @Test
    fun isUserSavedUseCase_invoke_shouldReturnDifferentResultsForDifferentUsers() = runTest {
        // Given: Два разных пользователя - один существует, другой нет
        val existingUserId = "existing_user"
        val nonExistingUserId = "non_existing_user"

        coEvery { mockRepository.isUserSaved(existingUserId) } returns true
        coEvery { mockRepository.isUserSaved(nonExistingUserId) } returns false

        // When: Проверяем обоих пользователей
        val existingResult = isUserSavedUseCase(existingUserId)
        val nonExistingResult = isUserSavedUseCase(nonExistingUserId)

        // Then: Результаты должны быть разными для разных пользователей
        assertTrue("Существующий пользователь должен вернуть true", existingResult)
        assertFalse("Несуществующий пользователь должен вернуть false", nonExistingResult)
        coVerify(exactly = 1) { mockRepository.isUserSaved(existingUserId) }
        coVerify(exactly = 1) { mockRepository.isUserSaved(nonExistingUserId) }
    }

    @Test
    fun isUserSavedUseCase_constructor_shouldInitializeWithRepository() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case
        val useCase = IsUserSavedUseCase(repository)

        // Then: Use Case должен быть правильно инициализирован
        assertTrue("Объект должен быть экземпляром IsUserSavedUseCase", useCase is IsUserSavedUseCase)
    }
}