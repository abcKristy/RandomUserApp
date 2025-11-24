package com.example.horseinacoat.domain

import com.example.horseinacoat.domain.secondary.Location
import com.example.horseinacoat.domain.secondary.Name
import com.example.horseinacoat.domain.secondary.Picture

data class User(
    val id: String,
    val gender: String,
    val name: Name,
    val location: Location,
    val email: String,
    val phone: String,
    val cell: String,
    val picture: Picture,
    val nat: String,
    val isSaved: Boolean = false
)