package com.example.horseinacoat.domain.secondary

data class Location(
    val street: Street,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String
)

data class Street(
    val number: Int,
    val name: String
) {
    val fullAddress: String
        get() = "$number $name"
}