// UsersStatistics.kt
package com.example.horseinacoat.domain.model

data class UsersStatistics(
    val totalUsers: Int = 0,
    val genderDistribution: Map<String, Int> = emptyMap(),
    val nationalityDistribution: Map<String, Int> = emptyMap(),
    val ageDistribution: Map<String, Int> = emptyMap(),
    val countryDistribution: Map<String, Int> = emptyMap(),
    val topCities: List<CityCount> = emptyList(),
    val averageAge: Double = 0.0,
    val newestUser: User? = null,
    val oldestUser: User? = null
)

data class CityCount(
    val city: String,
    val count: Int
)