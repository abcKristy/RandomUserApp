package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUsersPaginatedUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersPaginatedUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getUsersPaginatedUseCase: GetUsersPaginatedUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getUsersPaginatedUseCase = GetUsersPaginatedUseCase(mockRepository)
    }

    @Test
    fun getUsersPaginatedUseCase_invoke_shouldReturnSuccessWithUsers_whenRepositoryReturnsData() = runTest {
        // Given: Репозиторий возвращает пагинированный список пользователей
        val page = 0
        val pageSize = 10
        // Используем createTestUsers для создания нужного количества пользователей
        val expectedUsers = TestData.createTestUsers(pageSize)
        coEvery { mockRepository.getUsersPaginated(page, pageSize) } returns Result.Success(expectedUsers)

        // When: Запрашиваем первую страницу пользователей
        val result = getUsersPaginatedUseCase(page, pageSize)

        // Then: Должен вернуться успешный результат с правильным списком
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Должен вернуться ожидаемый список пользователей", expectedUsers, (result as Result.Success).data)
        assertEquals("Размер списка должен соответствовать pageSize", pageSize, (result as Result.Success).data.size)
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(page, pageSize) }
    }

    @Test
    fun getUsersPaginatedUseCase_invoke_shouldCalculateCorrectOffset_whenPageProvided() = runTest {
        // Given: Запрашиваем вторую страницу с размером 5
        val page = 2
        val pageSize = 5
        val expectedUsers = TestData.createTestUsers(pageSize)
        coEvery { mockRepository.getUsersPaginated(page, pageSize) } returns Result.Success(expectedUsers)

        // When: Вызываем Use Case для второй страницы
        val result = getUsersPaginatedUseCase(page, pageSize)

        // Then: Проверяем что репозиторий вызван с правильными параметрами пагинации
        assertTrue("Результат должен быть Success", result is Result.Success)
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(page, pageSize) }
    }

    @Test
    fun getUsersPaginatedUseCase_invoke_shouldReturnEmptyList_whenPageBeyondData() = runTest {
        // Given: Запрашиваем страницу за пределами доступных данных
        val page = 10
        val pageSize = 10
        coEvery { mockRepository.getUsersPaginated(page, pageSize) } returns Result.Success(emptyList())

        // When: Запрашиваем несуществующую страницу
        val result = getUsersPaginatedUseCase(page, pageSize)

        // Then: Должен вернуться успешный результат с пустым списком
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertTrue("Список должен быть пустым", (result as Result.Success).data.isEmpty())
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(page, pageSize) }
    }

    @Test
    fun getUsersPaginatedUseCase_invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given: Репозиторий возвращает ошибку при пагинации
        val page = 1
        val pageSize = 20
        val expectedException = Exception("Database pagination error")
        coEvery { mockRepository.getUsersPaginated(page, pageSize) } returns Result.Error(expectedException)

        // When: Запрашиваем страницу с ошибкой
        val result = getUsersPaginatedUseCase(page, pageSize)

        // Then: Должна вернуться ошибка с правильным сообщением
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно совпадать", expectedException.message, (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(page, pageSize) }
    }

    @Test
    fun getUsersPaginatedUseCase_invoke_shouldHandleDifferentPageSizes() = runTest {
        // Given: Тестируем разные размеры страниц
        val page = 0
        val smallPageSize = 5
        val largePageSize = 50
        val smallPageUsers = TestData.createTestUsers(smallPageSize)
        val largePageUsers = TestData.createTestUsers(largePageSize)

        coEvery { mockRepository.getUsersPaginated(page, smallPageSize) } returns Result.Success(smallPageUsers)
        coEvery { mockRepository.getUsersPaginated(page, largePageSize) } returns Result.Success(largePageUsers)

        // When: Запрашиваем страницы разного размера
        val smallResult = getUsersPaginatedUseCase(page, smallPageSize)
        val largeResult = getUsersPaginatedUseCase(page, largePageSize)

        // Then: Оба запроса должны вернуть правильное количество пользователей
        assertTrue("Малый результат должен быть Success", smallResult is Result.Success)
        assertTrue("Большой результат должен быть Success", largeResult is Result.Success)
        assertEquals("Малый размер должен совпадать", smallPageSize, (smallResult as Result.Success).data.size)
        assertEquals("Большой размер должен совпадать", largePageSize, (largeResult as Result.Success).data.size)
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(page, smallPageSize) }
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(page, largePageSize) }
    }

    @Test
    fun getUsersPaginatedUseCase_invoke_shouldCallRepositoryWithExactParameters() = runTest {
        // Given: Конкретные параметры пагинации
        val specificPage = 3
        val specificPageSize = 15
        coEvery { mockRepository.getUsersPaginated(specificPage, specificPageSize) } returns Result.Success(emptyList())

        // When: Вызываем Use Case с конкретными параметрами
        getUsersPaginatedUseCase(specificPage, specificPageSize)

        // Then: Репозиторий должен быть вызван с точными параметрами
        coVerify(exactly = 1) { mockRepository.getUsersPaginated(specificPage, specificPageSize) }
        coVerify(exactly = 0) { mockRepository.getAllUsers() }
    }

    @Test
    fun getUsersPaginatedUseCase_constructor_shouldInitializeWithRepository() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case
        val useCase = GetUsersPaginatedUseCase(repository)

        // Then: Use Case должен быть правильно инициализирован
        assertTrue("Объект должен быть экземпляром GetUsersPaginatedUseCase", useCase is GetUsersPaginatedUseCase)
    }
}