package com.example.horseinacoat.utils

import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.model.secondary.Dob
import com.example.horseinacoat.domain.model.secondary.Location
import com.example.horseinacoat.domain.model.secondary.Name
import com.example.horseinacoat.domain.model.secondary.Picture
import com.example.horseinacoat.domain.model.UsersStatistics
import com.example.horseinacoat.domain.model.CityCount
import com.example.horseinacoat.domain.model.secondary.Street

object TestData {

    val testUser = User(
        id = "1",
        gender = "male",
        name = Name("Mr", "John", "Doe"),
        location = Location(
            street = Street(123, "Main St"),
            city = "New York",
            state = "NY",
            country = "USA",
            postcode = "10001"
        ),
        email = "john.doe@example.com",
        phone = "+1-555-1234",
        cell = "+1-555-5678",
        picture = Picture(
            large = "https://example.com/large.jpg",
            medium = "https://example.com/medium.jpg",
            thumbnail = "https://example.com/thumbnail.jpg"
        ),
        nat = "US",
        dob = Dob("1990-01-01T00:00:00.000Z", 33)
    )

    val testUserList = listOf(
        testUser,
        testUser.copy(
            id = "2",
            name = Name("Ms", "Jane", "Smith"),
            gender = "female",
            location = testUser.location.copy(city = "Los Angeles"),
            nat = "CA"
        ),
        testUser.copy(
            id = "3",
            name = Name("Mr", "Bob", "Johnson"),
            location = testUser.location.copy(city = "Chicago"),
            nat = "GB",
            dob = Dob("1985-05-15T00:00:00.000Z", 38)
        ),
        testUser.copy(
            id = "4",
            name = Name("Mrs", "Alice", "Brown"),
            gender = "female",
            location = testUser.location.copy(city = "New York"),
            nat = "US",
            dob = Dob("1995-12-20T00:00:00.000Z", 28)
        )
    )

    val emptyUserList = emptyList<User>()

    val frenchUser = testUser.copy(
        id = "5",
        name = Name("M", "Pierre", "Dubois"),
        location = testUser.location.copy(city = "Paris", country = "France"),
        nat = "FR"
    )

    val testStatistics = UsersStatistics(
        totalUsers = 4,
        genderDistribution = mapOf(
            "male" to 2,
            "female" to 2
        ),
        nationalityDistribution = mapOf(
            "US" to 2,
            "CA" to 1,
            "GB" to 1
        ),
        ageDistribution = mapOf(
            "26-35" to 2,
            "36-50" to 2
        ),
        countryDistribution = mapOf(
            "USA" to 3,
            "France" to 1
        ),
        topCities = listOf(
            CityCount("New York", 2),
            CityCount("Los Angeles", 1),
            CityCount("Chicago", 1),
            CityCount("Paris", 1)
        ),
        averageAge = 32.5,
        newestUser = testUserList[3],
        oldestUser = testUserList[2]
    )

    val emptyStatistics = UsersStatistics()


    fun createTestUser(
        id: String = "1",
        gender: String = "male",
        firstName: String = "John",
        lastName: String = "Doe",
        city: String = "New York",
        country: String = "USA",
        nationality: String = "US",
        age: Int = 33
    ): User {
        return testUser.copy(
            id = id,
            gender = gender,
            name = Name("Mr", firstName, lastName),
            location = testUser.location.copy(city = city, country = country),
            nat = nationality,
            dob = Dob("1990-01-01T00:00:00.000Z", age)
        )
    }


    fun createTestUsers(count: Int): List<User> {
        return List(count) { index ->
            createTestUser(
                id = (index + 1).toString(),
                firstName = "User${index + 1}",
                city = "City${index % 5}",
                age = 20 + (index % 40)
            )
        }
    }
}