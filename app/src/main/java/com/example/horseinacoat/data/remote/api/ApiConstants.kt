package com.example.horseinacoat.data.remote.api

object ApiConstants {
    const val BASE_URL = "https://randomuser.me/"
    const val TIMEOUT_DURATION = 30L // seconds

    val SUPPORTED_NATIONALITIES = listOf(
        "AU", "BR", "CA", "CH", "DE", "DK", "ES", "FI", "FR", "GB",
        "IE", "IN", "IR", "MX", "NL", "NO", "NZ", "RS", "TR", "UA", "US"
    )

    const val DEFAULT_INCLUDE_FIELDS = "gender,name,location,email,phone,cell,picture,nat,login"
}