package com.planapp.qplanzaso.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsuarioViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    init {
        obtenerDatosUsuario()
    }

    fun obtenerDatosUsuario() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    val snapshot = firestore.collection("usuarios")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    val user = snapshot.toObject(Usuario::class.java)
                    _usuario.value = user
                } catch (e: Exception) {
                    e.printStackTrace()
                    _usuario.value = null
                }
            } else {
                _usuario.value = null
            }
        }
    }

    fun cerrarSesion() {
        auth.signOut()
        _usuario.value = null
    }
}
