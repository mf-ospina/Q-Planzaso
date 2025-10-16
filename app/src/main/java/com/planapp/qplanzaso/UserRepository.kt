// UserRepository.kt
package com.planapp.qplanzaso

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Crear usuario en Auth y en Firestore
    fun registrarUsuario(
        email: String,
        password: String,
        nombre: String,
        tipoUsuario: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val userMap = hashMapOf(
                    "uid" to uid,
                    "nombre" to nombre,
                    "email" to email,
                    "tipoUsuario" to tipoUsuario,
                    "fechaRegistro" to System.currentTimeMillis()
                )
                db.collection("usuarios").document(uid).set(userMap)
                    .addOnSuccessListener { callback(true, null) }
                    .addOnFailureListener { callback(false, it.message) }
            }
            .addOnFailureListener { callback(false, it.message) }
    }

    fun iniciarSesion(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { callback(false, it.message) }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}
