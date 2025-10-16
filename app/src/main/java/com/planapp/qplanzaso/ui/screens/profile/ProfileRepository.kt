package com.planapp.qplanzaso.ui.screens.profile

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val uid: String,
    private val db: FirebaseFirestore
) {
    private fun docRef() = db.collection("profiles").document(uid)

    // Flujo en tiempo real con el documento del perfil
    val data: Flow<ProfileFormState> = callbackFlow {
        val listener = docRef().addSnapshotListener { snap, _ ->
            val m = snap?.data.orEmpty()
            val state = ProfileFormState(
                nombre    = m["nombre"] as? String ?: "Nombre",
                apellido  = m["apellido"] as? String ?: "Apellido",
                email     = m["email"] as? String ?: "correo@dominio.com",
                telefono  = m["telefono"] as? String ?: "",
                ubicacion = m["ubicacion"] as? String ?: "Bogotá",
                bio       = m["bio"] as? String ?: ""
            )
            trySend(state).isSuccess
        }
        awaitClose { listener.remove() }
    }

    suspend fun save(s: ProfileFormState) {
        val map = mapOf(
            "nombre" to s.nombre,
            "apellido" to s.apellido,
            "email" to s.email,
            "telefono" to s.telefono,
            "ubicacion" to s.ubicacion,
            "bio" to s.bio
        )
        docRef().set(map).await()
    }
}
