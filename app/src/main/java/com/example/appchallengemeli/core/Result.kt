package com.example.appchallengemeli.core

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: AppException) : Result<Nothing>
}
