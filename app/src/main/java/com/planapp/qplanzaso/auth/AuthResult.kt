package com.planapp.qplanzaso.auth

/**
 * Estado genérico para manejar los resultados de autenticación o peticiones asíncronas.
 */
sealed class AuthResult<out T> {
    data class Success<out T>(val data: T?) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}
