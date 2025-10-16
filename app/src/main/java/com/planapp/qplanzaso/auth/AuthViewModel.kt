package com.planapp.qplanzaso.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult<Any>>(AuthResult.Idle)
    val authState: StateFlow<AuthResult<Any>> = _authState

    fun register(email: String, password: String, nombre: String, tipoUsuario: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading

            val result = repository.registerUser(
                email = email,
                password = password,
                nombre = nombre,
                tipoUsuario = tipoUsuario
            )

            _authState.value = result

            // 🔹 Esperamos un poco y reiniciamos el estado para que el botón se habilite de nuevo
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

    fun getCurrentUser(): FirebaseUser? = repository.currentUser()
}
