package com.example.horseinacoat.domain.repository

interface CacheRepository {

    suspend fun clearCache(): Result<Unit>

    suspend fun getCacheSize(): Long
}