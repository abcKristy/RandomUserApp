package com.example.horseinacoat

import com.example.horseinacoat.utils.TestData
import com.example.horseinacoat.utils.TestDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class SetupTest {

    @Test
    fun testDispatchersShouldBeInitialized() {
        val testDispatchers = TestDispatchers()

        assertNotNull(testDispatchers.main)
        assertNotNull(testDispatchers.io)
        assertNotNull(testDispatchers.default)
    }

    @Test
    fun testDataShouldContainValidUsers() {
        val user = TestData.testUser

        assertEquals("1", user.id)
        assertEquals("male", user.gender)
        assertEquals("John", user.name.first)
        assertEquals("Doe", user.name.last)
        assertEquals("New York", user.location.city)
        assertEquals("USA", user.location.country)
    }

    @Test
    fun testUserListShouldHaveCorrectSize() {
        assertEquals(4, TestData.testUserList.size)
    }

    @Test
    fun createTestUserShouldGenerateCustomUser() = runTest {
        val customUser = TestData.createTestUser(
            id = "100",
            firstName = "Custom",
            lastName = "User",
            city = "Berlin",
            age = 25
        )

        assertEquals("100", customUser.id)
        assertEquals("Custom", customUser.name.first)
        assertEquals("Berlin", customUser.location.city)
        assertEquals(25, customUser.dob?.age)
    }

    @Test
    fun createTestUsersShouldGenerateCorrectNumberOfUsers() {
        val users = TestData.createTestUsers(5)
        assertEquals(5, users.size)
        assertEquals("User3", users[2].name.first)
    }
}