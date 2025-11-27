package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveUserUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var saveUserUseCase: SaveUserUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        saveUserUseCase = SaveUserUseCase(mockRepository)
    }

    @Test
    fun saveUserUseCase_invoke_shouldReturnSuccess_whenUserSavedSuccessfully() = runTest {
        // Given: Пользователь успешно сохраняется в репозитории
        val testUser = TestData.testUser
        coEvery { mockRepository.saveUser(testUser) } returns Result.Success(Unit)

        // When: Сохраняем пользователя
        val result = saveUserUseCase(testUser)

        // Then: Должен вернуться успешный результат
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Данные результата должны быть Unit", Unit, (result as Result.Success).data)
        coVerify(exactly = 1) { mockRepository.saveUser(testUser) }
    }

    @Test
    fun saveUserUseCase_invoke_shouldReturnError_whenRepositoryFailsToSave() = runTest {
        // Given: Репозиторий возвращает ошибку при сохранении
        val testUser = TestData.testUser
        val expectedException = Exception("Failed to save user: Database error")
        coEvery { mockRepository.saveUser(testUser) } returns Result.Error(expectedException)

        // When: Пытаемся сохранить пользователя при ошибке
        val result = saveUserUseCase(testUser)

        // Then: Должна вернуться ошибка с правильным сообщением
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно совпадать", expectedException.message, (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.saveUser(testUser) }
    }

    @Test
    fun saveUserUseCase_invoke_shouldPassCorrectUserToRepository() = runTest {
        // Given: Конкретный пользователь для сохранения
        val specificUser = TestData.createTestUser(
            id = "specific_user_123",
            firstName = "Specific",
            lastName = "User",
            city = "Berlin"
        )
        coEvery { mockRepository.saveUser(specificUser) } returns Result.Success(Unit)

        // When: Сохраняем конкретного пользователя
        saveUserUseCase(specificUser)

        // Then: Репозиторий должен быть вызван с правильным пользователем
        coVerify(exactly = 1) { mockRepository.saveUser(specificUser) }
        coVerify(exactly = 0) { mockRepository.saveUser(TestData.testUser) } // Другой пользователь не должен сохраняться
    }

    @Test
    fun saveUserUseCase_invoke_shouldHandleDifferentUserTypes() = runTest {
        // Given: Разные типы пользователей для сохранения
        val maleUser = TestData.createTestUser(id = "male_user", gender = "male")
        val femaleUser = TestData.createTestUser(id = "female_user", gender = "female")
        val frenchUser = TestData.frenchUser

        coEvery { mockRepository.saveUser(maleUser) } returns Result.Success(Unit)
        coEvery { mockRepository.saveUser(femaleUser) } returns Result.Success(Unit)
        coEvery { mockRepository.saveUser(frenchUser) } returns Result.Success(Unit)

        // When: Сохраняем разных пользователей
        val maleResult = saveUserUseCase(maleUser)
        val femaleResult = saveUserUseCase(femaleUser)
        val frenchResult = saveUserUseCase(frenchUser)

        // Then: Все пользователи должны быть успешно сохранены
        assertTrue("Мужской пользователь должен быть сохранен", maleResult is Result.Success)
        assertTrue("Женский пользователь должен быть сохранен", femaleResult is Result.Success)
        assertTrue("Французский пользователь должен быть сохранен", frenchResult is Result.Success)

        coVerify(exactly = 1) { mockRepository.saveUser(maleUser) }
        coVerify(exactly = 1) { mockRepository.saveUser(femaleUser) }
        coVerify(exactly = 1) { mockRepository.saveUser(frenchUser) }
    }

    @Test
    fun saveUserUseCase_invoke_shouldPropagateRepositoryExceptions() = runTest {
        // Given: Репозиторий выбрасывает исключение при сохранении
        val testUser = TestData.testUser
        val databaseException = RuntimeException("Database constraint violation")
        coEvery { mockRepository.saveUser(testUser) } returns Result.Error(databaseException)

        // When: Сохраняем пользователя с исключением
        val result = saveUserUseCase(testUser)

        // Then: Исключение должно быть корректно обернуто в Result.Error
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Исключение должно совпадать", databaseException, (result as Result.Error).exception)
        coVerify(exactly = 1) { mockRepository.saveUser(testUser) }
    }

    @Test
    fun saveUserUseCase_invoke_shouldCallRepositoryExactlyOncePerUser() = runTest {
        // Given: Настраиваем мок для нескольких вызовов
        val user1 = TestData.createTestUser(id = "user1")
        val user2 = TestData.createTestUser(id = "user2")

        coEvery { mockRepository.saveUser(user1) } returns Result.Success(Unit)
        coEvery { mockRepository.saveUser(user2) } returns Result.Success(Unit)

        // When: Сохраняем нескольких пользователей
        saveUserUseCase(user1)
        saveUserUseCase(user2)

        // Then: Каждый пользователь должен быть сохранен ровно один раз
        coVerify(exactly = 1) { mockRepository.saveUser(user1) }
        coVerify(exactly = 1) { mockRepository.saveUser(user2) }
        coVerify(exactly = 0) { mockRepository.deleteUser(any()) }
        coVerify(exactly = 0) { mockRepository.getAllUsers() }
    }

    @Test
    fun saveUserUseCase_constructor_shouldInitializeWithRepository() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case
        val useCase = SaveUserUseCase(repository)

        // Then: Use Case должен быть правильно инициализирован
        assertTrue("Объект должен быть экземпляром SaveUserUseCase", useCase is SaveUserUseCase)
    }
}