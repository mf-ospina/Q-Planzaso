package com.planapp.qplanzaso.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.planapp.qplanzaso.data.repository.UsuarioRepository
import com.planapp.qplanzaso.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val usuarioRepository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult<Any>>(AuthResult.Loading)
    val authState: StateFlow<AuthResult<Any>> = _authState

    // ---------------------------
    // 1️⃣ Registrar nuevo usuario
    // ---------------------------
    fun register(email: String, password: String, nombre: String, rol: String = "asistente") {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading

            val result = authRepository.registerUser(email, password, nombre, rol)
            _authState.value = result

            if (result is AuthResult.Success && result.data != null) {
                val usuario = result.data
                try {
                    usuarioRepository.crearUsuario(usuario)
                    Log.d("AuthViewModel", "✅ Usuario guardado en Firestore correctamente.")
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "❌ Error al guardar usuario en Firestore", e)
                }
            }
        }
    }

    // ---------------------------
    // 2️⃣ Iniciar sesión
    // ---------------------------
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            _authState.value = authRepository.loginUser(email, password)
        }
    }

    // ---------------------------
    // 3️⃣ Cerrar sesión
    // ---------------------------
    fun logout() {
        authRepository.logoutUser()
        _authState.value = AuthResult.Success(null)
    }

    // ---------------------------
    // 4️⃣ Restablecer contraseña
    // ---------------------------
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = authRepository.resetPassword(email)
        }
    }

    // ---------------------------
    // 5️⃣ Obtener usuario actual
    // ---------------------------
    fun getCurrentUser(): FirebaseUser? = authRepository.currentUser()
}
