package com.example.horseinacoat.usecase

import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetUsersWithFiltersUseCase
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUsersWithFiltersUseCaseTest {

    private lateinit var mockRepository: UserRepository
    private lateinit var getUsersWithFiltersUseCase: GetUsersWithFiltersUseCase

    @Before
    fun setUp() {
        mockRepository = mockk(relaxed = true)
        getUsersWithFiltersUseCase = GetUsersWithFiltersUseCase(mockRepository)
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldReturnSuccessWithFilteredUsers_whenRepositoryReturnsData() = runTest {
        // Given: Репозиторий возвращает отфильтрованный список пользователей
        val count = 5
        val gender = "female"
        val nationality = "FR"
        val expectedUsers = listOf(TestData.frenchUser)
        coEvery { mockRepository.getUsersWithFilters(count, gender, nationality) } returns Result.Success(expectedUsers)

        // When: Запрашиваем пользователей с фильтрами по полу и национальности
        val result = getUsersWithFiltersUseCase(count, gender, nationality)

        // Then: Должен вернуться успешный результат с отфильтрованным списком
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertEquals("Должен вернуться ожидаемый список пользователей", expectedUsers, (result as Result.Success).data)
        assertEquals("Размер списка должен соответствовать количеству", 1, (result as Result.Success).data.size)
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(count, gender, nationality) }
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldUseDefaultParameters_whenNoArgumentsProvided() = runTest {
        // Given: Use Case вызывается без параметров (должны использоваться значения по умолчанию)
        val defaultCount = 1
        val defaultGender = null
        val defaultNationality = null
        val expectedUsers = listOf(TestData.testUser)
        coEvery { mockRepository.getUsersWithFilters(defaultCount, defaultGender, defaultNationality) } returns Result.Success(expectedUsers)

        // When: Вызываем Use Case без аргументов
        val result = getUsersWithFiltersUseCase()

        // Then: Должны использоваться параметры по умолчанию
        assertTrue("Результат должен быть Success", result is Result.Success)
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(defaultCount, defaultGender, defaultNationality) }
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldHandleGenderFilterOnly() = runTest {
        // Given: Фильтруем только по полу
        val count = 10
        val gender = "male"
        val nationality = null
        val maleUsers = TestData.testUserList.filter { it.gender == "male" }
        coEvery { mockRepository.getUsersWithFilters(count, gender, nationality) } returns Result.Success(maleUsers)

        // When: Запрашиваем пользователей только с фильтром по полу
        val result = getUsersWithFiltersUseCase(count, gender, null)

        // Then: Должны вернуться только пользователи мужского пола
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertTrue("Все пользователи должны быть мужского пола",
            (result as Result.Success).data.all { it.gender == "male" })
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(count, gender, nationality) }
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldHandleNationalityFilterOnly() = runTest {
        // Given: Фильтруем только по национальности
        val count = 8
        val gender = null
        val nationality = "US"
        val usUsers = TestData.testUserList.filter { it.nat == "US" }
        coEvery { mockRepository.getUsersWithFilters(count, gender, nationality) } returns Result.Success(usUsers)

        // When: Запрашиваем пользователей только с фильтром по национальности
        val result = getUsersWithFiltersUseCase(count, null, nationality)

        // Then: Должны вернуться только пользователи из США
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertTrue("Все пользователи должны быть из США",
            (result as Result.Success).data.all { it.nat == "US" })
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(count, gender, nationality) }
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldReturnEmptyList_whenNoUsersMatchFilters() = runTest {
        // Given: Нет пользователей, соответствующих фильтрам
        val count = 5
        val gender = "female"
        val nationality = "JP" // Японские женщины - нет в тестовых данных
        coEvery { mockRepository.getUsersWithFilters(count, gender, nationality) } returns Result.Success(emptyList())

        // When: Запрашиваем пользователей с несуществующими фильтрами
        val result = getUsersWithFiltersUseCase(count, gender, nationality)

        // Then: Должен вернуться успешный результат с пустым списком
        assertTrue("Результат должен быть Success", result is Result.Success)
        assertTrue("Список должен быть пустым", (result as Result.Success).data.isEmpty())
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(count, gender, nationality) }
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldReturnError_whenRepositoryFails() = runTest {
        // Given: Репозиторий возвращает ошибку при фильтрации
        val count = 3
        val gender = "male"
        val nationality = "US"
        val expectedException = Exception("API filter error")
        coEvery { mockRepository.getUsersWithFilters(count, gender, nationality) } returns Result.Error(expectedException)

        // When: Запрашиваем пользователей с фильтрами при ошибке репозитория
        val result = getUsersWithFiltersUseCase(count, gender, nationality)

        // Then: Должна вернуться ошибка с правильным сообщением
        assertTrue("Результат должен быть Error", result is Result.Error)
        assertEquals("Сообщение об ошибке должно совпадать", expectedException.message, (result as Result.Error).exception.message)
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(count, gender, nationality) }
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldHandleDifferentCountValues() = runTest {
        // Given: Тестируем разные значения количества пользователей
        val smallCount = 1
        val largeCount = 50
        val smallResultUsers = listOf(TestData.testUser)
        val largeResultUsers = TestData.createTestUsers(largeCount)

        coEvery { mockRepository.getUsersWithFilters(smallCount, any(), any()) } returns Result.Success(smallResultUsers)
        coEvery { mockRepository.getUsersWithFilters(largeCount, any(), any()) } returns Result.Success(largeResultUsers)

        // When: Запрашиваем разное количество пользователей
        val smallResult = getUsersWithFiltersUseCase(smallCount, "male", "US")
        val largeResult = getUsersWithFiltersUseCase(largeCount, "female", "FR")

        // Then: Оба запроса должны вернуть правильное количество пользователей
        assertTrue("Малый результат должен быть Success", smallResult is Result.Success)
        assertTrue("Большой результат должен быть Success", largeResult is Result.Success)
        assertEquals("Малый размер должен совпадать", smallCount, (smallResult as Result.Success).data.size)
        assertEquals("Большой размер должен совпадать", largeCount, (largeResult as Result.Success).data.size)
    }

    @Test
    fun getUsersWithFiltersUseCase_invoke_shouldCallRepositoryWithExactFilterParameters() = runTest {
        // Given: Конкретные параметры фильтрации
        val specificCount = 7
        val specificGender = "female"
        val specificNationality = "DE"
        coEvery { mockRepository.getUsersWithFilters(specificCount, specificGender, specificNationality) } returns Result.Success(emptyList())

        // When: Вызываем Use Case с конкретными параметрами фильтрации
        getUsersWithFiltersUseCase(specificCount, specificGender, specificNationality)

        // Then: Репозиторий должен быть вызван с точными параметрами фильтрации
        coVerify(exactly = 1) { mockRepository.getUsersWithFilters(specificCount, specificGender, specificNationality) }
        coVerify(exactly = 0) { mockRepository.getRandomUser(any(), any()) }
    }

    @Test
    fun getUsersWithFiltersUseCase_constructor_shouldInitializeWithRepository() {
        // Given: Мок репозитория
        val repository = mockk<UserRepository>()

        // When: Создаем Use Case
        val useCase = GetUsersWithFiltersUseCase(repository)

        // Then: Use Case должен быть правильно инициализирован
        assertTrue("Объект должен быть экземпляром GetUsersWithFiltersUseCase", useCase is GetUsersWithFiltersUseCase)
    }
}