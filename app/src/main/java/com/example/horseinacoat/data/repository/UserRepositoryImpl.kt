package com.example.horseinacoat.data.repository

import com.example.horseinacoat.data.local.dao.UserDao
import com.example.horseinacoat.data.mapper.UserEntityMapper
import com.example.horseinacoat.data.mapper.UserMapper
import com.example.horseinacoat.data.mapper.UserMapper.toDomainUserList
import com.example.horseinacoat.data.remote.api.UserApiService
import com.example.horseinacoat.data.remote.api.ApiErrorHandler
import com.example.horseinacoat.domain.model.CityCount
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.model.Result
import com.example.horseinacoat.domain.model.UsersStatistics
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getRandomUser(
        gender: String?,
        nationality: String?
    ): Result<User> {
        return try {
            val apiResponse = userApiService.getRandomUser(
                gender = gender,
                nationality = nationality
            )

            val user = UserMapper.run {
                apiResponse.toDomainUser()
            } ?: return Result.Error(Exception("No user data received"))

            Result.Success(user)

        } catch (e: Exception) {
            val cachedUsers = userDao.getAllUsers()
            if (cachedUsers.isNotEmpty()) {
                val cachedUser = UserEntityMapper.run {
                    cachedUsers.first().toDomain()
                }
                Result.Success(cachedUser)
            } else {
                Result.Error(Exception(ApiErrorHandler.handleException(e)))
            }
        }
    }
    override suspend fun getUsersPaginated(
        page: Int,
        pageSize: Int
    ): Result<List<User>> {
        return try {
            val offset = page * pageSize
            val userEntities = userDao.getUsersPaginated(pageSize, offset)
            val domainUsers = UserEntityMapper.run {
                userEntities.toDomain()
            }
            Result.Success(domainUsers)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to load paginated users: ${e.message}"))
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val users = userDao.getAllUsers()
            val domainUsers = UserEntityMapper.run {
                users.toDomain()
            }
            Result.Success(domainUsers)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to load users from database: ${e.message}"))
        }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val userEntity = UserEntityMapper.run {
                user.toEntity()
            }
            userDao.insertUser(userEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to save user: ${e.message}"))
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            userDao.deleteUserById(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to delete user: ${e.message}"))
        }
    }

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val userEntity = userDao.getUserById(userId)
            if (userEntity != null) {
                val user = UserEntityMapper.run {
                    userEntity.toDomain()
                }
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Failed to get user: ${e.message}"))
        }
    }

    override suspend fun isUserSaved(userId: String): Boolean {
        return try {
            val count = userDao.isUserExists(userId)
            count > 0
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUsersWithFilters(
        count: Int,
        gender: String?,
        nationality: String?
    ): Result<List<User>> = runCatching {
        userApiService.getUsersWithFilters(
            count = count,
            gender = gender,
            nationality = nationality
        ).toDomainUserList()
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = {
            Result.Error(Exception(ApiErrorHandler.handleException(it as Exception)))
        }
    )

    override suspend fun getUsersStatistics(): Result<UsersStatistics> {
        return try {
            val users = userDao.getAllUsers()
            val domainUsers = UserEntityMapper.run {
                users.toDomain()
            }

            val statistics = calculateStatistics(domainUsers)
            Result.Success(statistics)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to calculate statistics: ${e.message}"))
        }
    }

    private fun calculateStatistics(users: List<User>): UsersStatistics {
        if (users.isEmpty()) return UsersStatistics()

        val genderDistribution = users.groupingBy { it.gender }.eachCount()

        val nationalityDistribution = users.groupingBy { it.nat }.eachCount()

        val ageDistribution = users.groupingBy { user ->
            when (val age = user.dob?.age ?: 0) {
                in 0..17 -> "0-17"
                in 18..25 -> "18-25"
                in 26..35 -> "26-35"
                in 36..50 -> "36-50"
                else -> "50+"
            }
        }.eachCount()

        val countryDistribution = users.groupingBy { it.location.country }.eachCount()

        val topCities = users.groupingBy { it.location.city }
            .eachCount()
            .map { CityCount(it.key, it.value) }
            .sortedByDescending { it.count }
            .take(10)

        val averageAge = users.mapNotNull { it.dob?.age }.average()

        val usersWithAge = users.filter { it.dob?.age != null }
        val newestUser = usersWithAge.maxByOrNull { it.dob?.age ?: 0 }
        val oldestUser = usersWithAge.minByOrNull { it.dob?.age ?: 0 }

        return UsersStatistics(
            totalUsers = users.size,
            genderDistribution = genderDistribution,
            nationalityDistribution = nationalityDistribution,
            ageDistribution = ageDistribution,
            countryDistribution = countryDistribution,
            topCities = topCities,
            averageAge = averageAge,
            newestUser = newestUser,
            oldestUser = oldestUser
        )
    }
}