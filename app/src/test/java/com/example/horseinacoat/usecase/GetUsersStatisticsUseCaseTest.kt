package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUsersStatisticsUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersStatisticsUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getUsersStatisticsUseCase: GetUsersStatisticsUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getUsersStatisticsUseCase = GetUsersStatisticsUseCase(mockRepository)
    }

    @Test
    fun getUsersStatisticsUseCase_invoke_shouldReturnSuccessWithStatistics_whenRepositoryHasData() = runTest {
        // Given: Репозиторий возвращает полную статистику по пользователям
        val expectedStatistics = TestData.testStatistics
        coEvery { mockRepository.getUsersStatistics() } returns Result.Success(expectedStatistics)

        // When: Запрашиваем статистику пользователей
        val result = getUsersStatisticsUseCase()

        // Then: Должен вернуться успешный результат с полной статистикой
        assertTrue("Результат должен быть Success", result is Result.Success)
        val actualStatistics = (result as Result.Success).data
        assertEquals("Общее количество пользователей должно совпадать", 4, actualStatistics.totalUsers)
        assertEquals("Распределение по полу должно совпадать", mapOf("male" to 2, "female" to 2), actualStatistics.genderDistribution)
        assertEquals("Средний возраст должен совпадать", 32.5, actualStatistics.averageAge, 0.01)
        assertEquals("Количество топ городов должно совпадать", 4, actualStatistics.topCities.size)
        coVerify(exactly = 1) { mockRepository.getUsersStatistics() }
    }

    @Test
    fun getUsersStatisticsUseCase_invoke_shouldReturnEmptyStatistics_whenNoUsers() = runTest {
        // Given: В репозитории нет пользователей (пустая статистика)
        val emptyStatistics = TestData.emptyStatistics
        coEvery { mockRepository.getUsersStatistics() } returns Result.Success(emptyStatistics)

        // When: Запрашиваем статистику при отсутствии пользователей
        val result = getUsersStatisticsUseCase()

        // Then: Должна вернуться пустая статистика с нулевыми значениями
        assertTrue("Результат должен быть Success", result is Result.Success)
        val actualStatistics = (result as Result.Success).data
        assertEquals("Общее количество пользователей должно быть 0", 0, actualStatistics.totalUsers)
        assertTrue("Распределение по полу должно быть пустым", actualStatistics.genderDistribution.isEmpty())
        assertEquals("Средний возраст должен быть 0", 0.0, actualStatistics.averageAge, 0.01)
        assertTrue("Список топ городов должен быть пустым", actualStatistics.topCities.isEmpty())
        assertTrue("Новейший пользователь должен быть null", actualStatistics.newestUser == null)
        assertTrue("Старейший пользователь должен быть null", actualStatistics.oldestUser == null)
        coVerify(exactly = 1) { mockRepository.getUsersStatistics() }
    }

    @Test
    fun getUsersStatisticsUseCase_invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given: Репозиторий возвращает ошибку при расчете статистики
        val expectedException = Exception("Failed to calculate statistics: Database error")
        coEvery { mockRepository.getUsersStatistics() } returns Result.Error(expectedException)

        // When: Запрашиваем статистику при ошибке репозитория
        val result = getUsersStatisticsUseCase()

        // Then: Должна вернуться ошибка с правильным сообщением
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно совпадать", expectedException.message, (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.getUsersStatistics() }
    }

    @Test
    fun getUsersStatisticsUseCase_invoke_shouldReturnCorrectAgeDistribution() = runTest {
        // Given: Статистика содержит корректное распределение по возрастам
        val statisticsWithAgeDistribution = TestData.testStatistics.copy(
            ageDistribution = mapOf(
                "0-17" to 2,
                "18-25" to 5,
                "26-35" to 8,
                "36-50" to 3,
                "50+" to 1
            )
        )
        coEvery { mockRepository.getUsersStatistics() } returns Result.Success(statisticsWithAgeDistribution)

        // When: Запрашиваем статистику с распределением по возрастам
        val result = getUsersStatisticsUseCase()

        // Then: Распределение по возрастам должно быть правильным
        assertTrue("Результат должен быть Success", result is Result.Success)
        val ageDistribution = (result as Result.Success).data.ageDistribution
        assertEquals("Количество пользователей 0-17 лет должно быть 2", 2, ageDistribution["0-17"])
        assertEquals("Количество пользователей 18-25 лет должно быть 5", 5, ageDistribution["18-25"])
        assertEquals("Количество пользователей 26-35 лет должно быть 8", 8, ageDistribution["26-35"])
        assertEquals("Количество пользователей 36-50 лет должно быть 3", 3, ageDistribution["36-50"])
        assertEquals("Количество пользователей 50+ лет должно быть 1", 1, ageDistribution["50+"])
    }

    @Test
    fun getUsersStatisticsUseCase_invoke_shouldReturnCorrectTopCities() = runTest {
        // Given: Статистика содержит список топ городов
        val statisticsWithTopCities = TestData.testStatistics.copy(
            topCities = listOf(
                TestData.createCityCount("New York", 15),
                TestData.createCityCount("Los Angeles", 12),
                TestData.createCityCount("Chicago", 8),
                TestData.createCityCount("Miami", 5)
            )
        )
        coEvery { mockRepository.getUsersStatistics() } returns Result.Success(statisticsWithTopCities)

        // When: Запрашиваем статистику с топ городами
        val result = getUsersStatisticsUseCase()

        // Then: Топ города должны быть отсортированы по убыванию и содержать правильные данные
        assertTrue("Результат должен быть Success", result is Result.Success)
        val topCities = (result as Result.Success).data.topCities
        assertEquals("Количество топ городов должно быть 4", 4, topCities.size)
        assertEquals("Первый город должен быть New York", "New York", topCities[0].city)
        assertEquals("Количество пользователей в New York должно быть 15", 15, topCities[0].count)
        assertEquals("Последний город должен быть Miami", "Miami", topCities[3].city)
        assertEquals("Количество пользователей в Miami должно быть 5", 5, topCities[3].count)
    }

    @Test
    fun getUsersStatisticsUseCase_invoke_shouldCallRepositoryExactlyOnce() = runTest {
        // Given: Настраиваем мок репозитория
        coEvery { mockRepository.getUsersStatistics() } returns Result.Success(TestData.testStatistics)

        // When: Вызываем Use Case для получения статистики
        getUsersStatisticsUseCase()

        // Then: Репозиторий должен быть вызван ровно один раз
        coVerify(exactly = 1) { mockRepository.getUsersStatistics() }
        coVerify(exactly = 0) { mockRepository.getAllUsers() }
        coVerify(exactly = 0) { mockRepository.getRandomUser(any(), any()) }
    }

    @Test
    fun getUsersStatisticsUseCase_constructor_shouldInitializeWithRepository() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case
        val useCase = GetUsersStatisticsUseCase(repository)

        // Then: Use Case должен быть правильно инициализирован
        assertTrue("Объект должен быть экземпляром GetUsersStatisticsUseCase", useCase is GetUsersStatisticsUseCase)
    }
}