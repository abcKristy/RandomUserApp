package com.example.horseinacoat.data.mapper

import com.example.horseinacoat.data.local.entity.UserEntity
import com.example.horseinacoat.domain.User
import com.example.horseinacoat.domain.secondary.Location
import com.example.horseinacoat.domain.secondary.Name
import com.example.horseinacoat.domain.secondary.Picture
import com.example.horseinacoat.domain.secondary.Street

object UserEntityMapper {

    fun User.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            gender = gender,
            title = name.title,
            firstName = name.first,
            lastName = name.last,
            streetNumber = location.street.number,
            streetName = location.street.name,
            city = location.city,
            state = location.state,
            country = location.country,
            postcode = location.postcode,
            email = email,
            phone = phone,
            cell = cell,
            pictureLarge = picture.large,
            pictureMedium = picture.medium,
            pictureThumbnail = picture.thumbnail,
            nationality = nat
        )
    }

    fun UserEntity.toDomain(): User {
        return User(
            id = id,
            gender = gender,
            name = Name(
                title = title,
                first = firstName,
                last = lastName
            ),
            location = Location(
                street = Street(
                    number = streetNumber,
                    name = streetName
                ),
                city = city,
                state = state,
                country = country,
                postcode = postcode
            ),
            email = email,
            phone = phone,
            cell = cell,
            picture = Picture(
                large = pictureLarge,
                medium = pictureMedium,
                thumbnail = pictureThumbnail
            ),
            nat = nationality,
            isSaved = true
        )
    }

    fun List<UserEntity>.toDomain(): List<User> {
        return map { it.toDomain() }
    }

    fun List<User>.toEntity(): List<UserEntity> {
        return map { it.toEntity() }
    }
}