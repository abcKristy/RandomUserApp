package com.example.horseinacoat.data.remote.model

data class UserDto(
    val gender: String,
    val name: NameDto,
    val location: LocationDto,
    val email: String,
    val login: LoginDto,
    val dob: DobDto,
    val registered: RegisteredDto,
    val phone: String,
    val cell: String,
    val id: IdDto?,
    val picture: PictureDto,
    val nat: String
)

data class NameDto(
    val title: String,
    val first: String,
    val last: String
)

data class LocationDto(
    val street: StreetDto,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String,
    val coordinates: CoordinatesDto,
    val timezone: TimezoneDto
)

data class StreetDto(
    val number: Int,
    val name: String
)

data class CoordinatesDto(
    val latitude: String,
    val longitude: String
)

data class TimezoneDto(
    val offset: String,
    val description: String
)

data class LoginDto(
    val uuid: String,
    val username: String,
    val password: String,
    val salt: String,
    val md5: String,
    val sha1: String,
    val sha256: String
)

data class DobDto(
    val date: String,
    val age: Int
)

data class RegisteredDto(
    val date: String,
    val age: Int
)

data class IdDto(
    val name: String,
    val value: String?
)

data class PictureDto(
    val large: String,
    val medium: String,
    val thumbnail: String
)