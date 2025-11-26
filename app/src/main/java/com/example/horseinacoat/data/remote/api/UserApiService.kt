package com.example.horseinacoat.data.remote.api

import com.example.horseinacoat.data.remote.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {

    @GET("api/")
    suspend fun getRandomUser(
        @Query("gender") gender: String? = null,
        @Query("nat") nationality: String? = null,
        @Query("results") results: Int = 1,
        @Query("seed") seed: String? = null,
        @Query("page") page: Int? = null,
        @Query("inc") includeFields: String? = null,
        @Query("exc") excludeFields: String? = null,
        @Query("noinfo") noInfo: Boolean? = null
    ): ApiResponse

    @GET("api/")
    suspend fun getUsersWithFilters(
        @Query("results") count: Int,
        @Query("gender") gender: String? = null,
        @Query("nat") nationality: String? = null
    ): ApiResponse
}