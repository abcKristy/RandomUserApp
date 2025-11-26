package com.example.horseinacoat.data.mapper

import com.example.horseinacoat.data.remote.model.ApiResponse
import com.example.horseinacoat.data.remote.model.UserDto
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.model.secondary.Dob
import com.example.horseinacoat.domain.model.secondary.Location
import com.example.horseinacoat.domain.model.secondary.Name
import com.example.horseinacoat.domain.model.secondary.Picture
import com.example.horseinacoat.domain.model.secondary.Street

object UserMapper {

    fun UserDto.toDomain(): User {
        return User(
            id = login.uuid,
            gender = gender,
            name = Name(
                title = name.title,
                first = name.first,
                last = name.last
            ),
            location = Location(
                street = Street(
                    number = location.street.number,
                    name = location.street.name
                ),
                city = location.city,
                state = location.state,
                country = location.country,
                postcode = location.postcode.toString()
            ),
            email = email,
            phone = phone,
            cell = cell,
            picture = Picture(
                large = picture.large,
                medium = picture.medium,
                thumbnail = picture.thumbnail
            ),
            nat = nat,
            dob = Dob(
                date = dob.date,
                age = dob.age
            ),
            isSaved = false
        )
    }

    fun ApiResponse.toDomainUser(): User? {
        return results.firstOrNull()?.toDomain()
    }

    fun ApiResponse.toDomainUserList(): List<User> {
        return results.map { it.toDomain() }
    }
}