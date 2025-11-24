package com.example.horseinacoat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.horseinacoat.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY created_at DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(users: List<UserEntity>)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT COUNT(*) FROM users WHERE id = :userId")
    suspend fun isUserExists(userId: String): Int

    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>
}