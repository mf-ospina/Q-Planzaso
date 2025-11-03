package com.planapp.qplanzaso.auth

/**
 * Estado genérico para manejar resultados de autenticación o peticiones asíncronas.
 */
sealed class AuthResult<out T> {
    object Idle : AuthResult<Nothing>()               // Estado inicial, sin acción
    object Loading : AuthResult<Nothing>()            // Cargando
    data class Success<out T>(val data: T?) : AuthResult<T>()   // Éxito
    data class Error(val message: String) : AuthResult<Nothing>() // Error
}
