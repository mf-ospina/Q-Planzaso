package com.planapp.qplanzaso.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.model.Usuario
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de autenticación y manejo de usuarios para Firebase.
 * - Crea y autentica usuarios con Firebase Auth.
 * - Guarda la información del usuario en Firestore (sin contraseñas).
 * - Permite login, logout y recuperación de contraseña.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Registra un nuevo usuario en Firebase Auth y lo almacena en Firestore.
     * @param email correo electrónico del usuario
     * @param password contraseña (solo se guarda en Firebase Auth)
     * @param nombre nombre del usuario
     * @param tipoUsuario "particular" o "empresarial"
     */
    suspend fun registerUser(
        email: String,
        password: String,
        nombre: String,
        tipoUsuario: String
    ): AuthResult<Usuario> {
        return try {
            // Crear usuario en Authentication
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("No se pudo crear el usuario.")

            // Crear objeto Usuario para Firestore
            val nuevoUsuario = Usuario(
                uid = firebaseUser.uid,
                nombre = nombre,
                correo = email,
                tipoUsuario = tipoUsuario,
                fechaRegistro = null,
                preferencias = listOf(),
                fotoPerfil = "",
                verified = tipoUsuario == "empresarial", // por ahora, marcamos empresariales como verificados
                eventosPublicados = 0
            )

            // Guardar usuario en Firestore
            val userMap = nuevoUsuario.toMap().toMutableMap()
            userMap["fechaRegistro"] = FieldValue.serverTimestamp()

            db.collection("usuarios")
                .document(firebaseUser.uid)
                .set(userMap)
                .await()

            AuthResult.Success(nuevoUsuario)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al registrar el usuario.")
        }
    }

    /**
     * Inicia sesión con correo y contraseña.
     */
    suspend fun loginUser(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Usuario no encontrado.")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido al iniciar sesión.")
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun logoutUser() {
        auth.signOut()
    }

    /**
     * Devuelve el usuario autenticado actualmente (si lo hay).
     */
    fun currentUser(): FirebaseUser? = auth.currentUser

    /**
     * Envía un correo de recuperación de contraseña.
     */
    suspend fun resetPassword(email: String): AuthResult<String> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success("Correo de recuperación enviado.")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al enviar el correo de recuperación.")
        }
    }
}
