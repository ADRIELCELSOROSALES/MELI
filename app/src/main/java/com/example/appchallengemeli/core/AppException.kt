package com.example.appchallengemeli.core

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    data object Network : AppException("No hay conexión a internet")
    data object Timeout : AppException("Tiempo de espera agotado")
    data class Api(val code: Int, val msg: String) : AppException("Error $code: $msg")
    data object TokenExpired : AppException("Token expirado. Reemplazá el token en AppConfig y recompilá.")
    data class Unknown(val throwable: Throwable) : AppException(
        throwable.message ?: "Error desconocido",
        throwable
    )
}
