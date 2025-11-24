package com.example.horseinacoat.data.remote.model

data class ApiResponse(
    val results: List<UserDto>,
    val info: InfoDto
)

data class InfoDto(
    val seed: String,
    val results: Int,
    val page: Int,
    val version: String
)