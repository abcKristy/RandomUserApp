package com.example.horseinacoat.domain.secondary

data class Name(
    val title: String,
    val first: String,
    val last: String
) {
    val fullName: String
        get() = "$first $last"
}