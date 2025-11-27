package com.example.horseinacoat.presentation.viewModel.custom

import app.cash.turbine.test
import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.utils.BaseViewModelTest
import com.example.horseinacoat.utils.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CustomListViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: CustomListViewModel
    private val getAllUsersUseCase: GetAllUsersUseCase = mockk()

    @Before
    fun setUp() {
        // Базовая настройка мока
        coEvery { getAllUsersUseCase() } returns Result.Success(emptyList())
    }

    @Test
    fun `should load users successfully`() = runTest {
        // Given
        val testUsers = TestData.testUserList
        coEvery { getAllUsersUseCase() } returns Result.Success(testUsers)

        // When
        viewModel = CustomListViewModel(getAllUsersUseCase)

        // Then
        viewModel.users.test {
            val users = awaitItem()
            assertEquals(testUsers, users)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should set error when loading fails`() = runTest {
        // Given
        val errorMessage = "Failed to load users"
        val exception = IOException(errorMessage)
        coEvery { getAllUsersUseCase() } returns Result.Error(exception)

        // When
        viewModel = CustomListViewModel(getAllUsersUseCase)

        // Then
        viewModel.error.test {
            val error = awaitItem()
            assertEquals(errorMessage, error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should have empty users when loading fails`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { getAllUsersUseCase() } returns Result.Error(exception)

        // When
        viewModel = CustomListViewModel(getAllUsersUseCase)

        // Then
        viewModel.users.test {
            val users = awaitItem()
            assertTrue(users.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle empty user list`() = runTest {
        // Given
        val emptyList = emptyList<User>()
        coEvery { getAllUsersUseCase() } returns Result.Success(emptyList)

        // When
        viewModel = CustomListViewModel(getAllUsersUseCase)

        // Then
        viewModel.users.test {
            val users = awaitItem()
            assertTrue(users.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should refresh users with new data`() = runTest {
        // Given
        val initialUsers = TestData.testUserList.take(2)
        val refreshedUsers = TestData.testUserList

        var callCount = 0
        coEvery { getAllUsersUseCase() } coAnswers {
            callCount++
            when (callCount) {
                1 -> Result.Success(initialUsers)
                2 -> Result.Success(refreshedUsers)
                else -> Result.Success(emptyList())
            }
        }

        // When - создаем ViewModel (первая загрузка)
        viewModel = CustomListViewModel(getAllUsersUseCase)

        // Then - проверяем первую загрузку
        viewModel.users.test {
            assertEquals(initialUsers, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // When - обновляем данные
        viewModel.refreshUsers()

        // Then - проверяем обновленные данные
        viewModel.users.test {
            assertEquals(refreshedUsers, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear error after successful refresh`() = runTest {
        // Given
        val errorMessage = "Initial error"
        val exception = RuntimeException(errorMessage)
        val testUsers = TestData.testUserList

        var callCount = 0
        coEvery { getAllUsersUseCase() } coAnswers {
            callCount++
            when (callCount) {
                1 -> Result.Error(exception)
                2 -> Result.Success(testUsers)
                else -> Result.Success(emptyList())
            }
        }

        // When - создаем ViewModel (первая загрузка с ошибкой)
        viewModel = CustomListViewModel(getAllUsersUseCase)

        // Then - проверяем ошибку
        viewModel.error.test {
            // Получаем ошибку
            val error = awaitItem()
            assertEquals(errorMessage, error)
            cancelAndIgnoreRemainingEvents()
        }

        // When - обновляем с успешным результатом
        viewModel.refreshUsers()

        // Then - ошибка должна очиститься
        viewModel.error.test {
            // Просто проверяем, что следующее значение - null
            val nextError = awaitItem()
            assertNull(nextError)
            cancelAndIgnoreRemainingEvents()
        }
    }
}