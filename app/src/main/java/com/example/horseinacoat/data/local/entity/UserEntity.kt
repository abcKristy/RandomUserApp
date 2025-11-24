package com.example.horseinacoat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "street_number")
    val streetNumber: Int,

    @ColumnInfo(name = "street_name")
    val streetName: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "postcode")
    val postcode: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "cell")
    val cell: String,

    @ColumnInfo(name = "picture_large")
    val pictureLarge: String,

    @ColumnInfo(name = "picture_medium")
    val pictureMedium: String,

    @ColumnInfo(name = "picture_thumbnail")
    val pictureThumbnail: String,

    @ColumnInfo(name = "nationality")
    val nationality: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)