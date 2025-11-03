package com.planapp.qplanzaso.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel para autenticación y manejo de sesión.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    // Estado de acciones de autenticación (registro, login, reset)
    private val _authState = MutableStateFlow<AuthResult<Any>>(AuthResult.Idle)
    val authState: StateFlow<AuthResult<Any>> = _authState

    // Estado reactivo del usuario actual
    private val _currentUser = MutableStateFlow(repository.currentUser())
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        // Escuchar cambios de autenticación automáticamente
        repository.auth.addAuthStateListener {
            _currentUser.value = repository.currentUser()
        }
    }

    fun register(email: String, password: String, nombre: String, tipoUsuario: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.registerUser(email, password, nombre, tipoUsuario)
            _authState.value = result
            kotlinx.coroutines.delay(100)
            _authState.value = AuthResult.Idle
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.loginUser(email, password)
            _authState.value = result
            kotlinx.coroutines.delay(100)
            _authState.value = AuthResult.Idle
        }
    }

    fun logout() {
        repository.logoutUser()
        _authState.value = AuthResult.Idle
        _currentUser.value = null
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = repository.resetPassword(email)
            _authState.value = result
            kotlinx.coroutines.delay(100)
            _authState.value = AuthResult.Idle
        }
    }
}
