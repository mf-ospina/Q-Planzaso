package com.planapp.qplanzaso.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun registerUser(
        email: String,
        password: String,
        nombre: String,
        rol: String = "asistente",
        tipoUsuario: String = "particular"
    ): AuthResult<Usuario> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("No se pudo crear el usuario.")

            val nuevoUsuario = Usuario(
                uid = firebaseUser.uid,
                nombre = nombre,
                correo = email,
                rol = rol,
                tipoUsuario = tipoUsuario,
                preferencias = listOf(),
                fotoPerfil = "",
                verified = false,
                eventosPublicados = 0
            )

            db.collection("usuarios").document(firebaseUser.uid).set(nuevoUsuario).await()
            AuthResult.Success(nuevoUsuario)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al registrar el usuario.")
        }
    }

    suspend fun loginUser(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Usuario no encontrado.")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al iniciar sesión.")
        }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    suspend fun resetPassword(email: String): AuthResult<String> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success("Correo de recuperación enviado.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al enviar el correo de recuperación.")
        }
    }
}