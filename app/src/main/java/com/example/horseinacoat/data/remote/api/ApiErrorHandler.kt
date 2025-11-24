package com.example.horseinacoat.data.remote.api

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object ApiErrorHandler {

    fun handleException(exception: Exception): String {
        return when (exception) {
            is SocketTimeoutException -> "Request timeout. Please check your internet connection."
            is IOException -> "Network error. Please check your internet connection."
            is HttpException -> {
                when (exception.code()) {
                    404 -> "Resource not found."
                    500 -> "Internal server error."
                    else -> "Something went wrong. Please try again."
                }
            }
            else -> "An unexpected error occurred: ${exception.message}"
        }
    }
}