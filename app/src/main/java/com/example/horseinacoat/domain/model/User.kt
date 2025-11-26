package com.example.horseinacoat.domain.model

import com.example.horseinacoat.domain.model.secondary.Dob
import com.example.horseinacoat.domain.model.secondary.Location
import com.example.horseinacoat.domain.model.secondary.Name
import com.example.horseinacoat.domain.model.secondary.Picture

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
    val dob: Dob? = null,
    val isSaved: Boolean = false
)