package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRandomUserUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getRandomUserUseCase: GetRandomUserUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getRandomUserUseCase = GetRandomUserUseCase(mockRepository)
    }

    @Test
    fun getRandomUserUseCase_invoke_shouldReturnSuccessWithUser_whenRepositoryReturnsUser() = runTest {
        // Given: Репозиторий возвращает случайного пользователя
        val expectedUser = TestData.testUser
        coEvery { mockRepository.getRandomUser(any(), any()) } returns Result.Success(expectedUser)

        // When: Вызываем Use Case без параметров
        val result = getRandomUserUseCase()

        // Then: Проверяем успешный результат с пользователем
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Должен вернуться ожидаемый пользователь", expectedUser, (result as Result.Success).data)
        coVerify(exactly = 1) { mockRepository.getRandomUser(null, null) }
    }

    @Test
    fun getRandomUserUseCase_invoke_shouldPassFiltersToRepository_whenParametersProvided() = runTest {
        // Given: Настраиваем репозиторий для фильтрованного запроса
        val gender = "female"
        val nationality = "FR"
        val expectedUser = TestData.frenchUser
        coEvery { mockRepository.getRandomUser(gender, nationality) } returns Result.Success(expectedUser)

        // When: Вызываем Use Case с параметрами фильтрации
        val result = getRandomUserUseCase(gender, nationality)

        // Then: Проверяем что фильтры переданы в репозиторий
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Должен вернуться французский пользователь", expectedUser, (result as Result.Success).data)
        coVerify(exactly = 1) { mockRepository.getRandomUser(gender, nationality) }
    }

    @Test
    fun getRandomUserUseCase_invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given: Репозиторий возвращает ошибку
        val expectedException = Exception("Network error")
        coEvery { mockRepository.getRandomUser(any(), any()) } returns Result.Error(expectedException)

        // When: Вызываем Use Case
        val result = getRandomUserUseCase()

        // Then: Проверяем что ошибка корректно передана
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно совпадать", expectedException.message, (result as Result.Error).exception.message)
    }

    @Test
    fun getRandomUserUseCase_invoke_shouldHandleNullParametersCorrectly() = runTest {
        // Given: Репозиторий принимает null параметры
        coEvery { mockRepository.getRandomUser(null, null) } returns Result.Success(TestData.testUser)

        // When: Вызываем Use Case с явными null
        val result = getRandomUserUseCase(null, null)

        // Then: Проверяем вызов с null параметрами
        assertTrue("Результат должен быть Success", result is Result.Success)
        coVerify(exactly = 1) { mockRepository.getRandomUser(null, null) }
    }

    @Test
    fun getRandomUserUseCase_invoke_shouldUseDefaultParameters_whenNoArguments() = runTest {
        // Given: Настраиваем вызов без параметров
        coEvery { mockRepository.getRandomUser(null, null) } returns Result.Success(TestData.testUser)

        // When: Вызываем Use Case без аргументов
        getRandomUserUseCase()

        // Then: Проверяем что использованы параметры по умолчанию (null)
        coVerify(exactly = 1) { mockRepository.getRandomUser(null, null) }
    }
}