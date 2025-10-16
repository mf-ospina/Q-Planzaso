// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/ProfileRepository.kt
package com.planapp.qplanzaso.ui.screens.profile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val uid: String,
    private val db: FirebaseFirestore
) {
    private val doc = db.collection("users").document(uid)

    /** Stream en tiempo real del perfil. */
    fun flow(): Flow<ProfileFormState> = callbackFlow {
        val reg: ListenerRegistration = doc.addSnapshotListener { snap, _ ->
            val m = snap?.data.orEmpty()
            val state = ProfileFormState(
                nombre    = m["nombre"] as? String ?: "Nombre",
                apellido  = m["apellido"] as? String ?: "Apellido",
                email     = m["email"] as? String ?: "correo@dominio.com",
                telefono  = m["telefono"] as? String ?: "",
                ubicacion = m["ubicacion"] as? String ?: "Bogotá",
                bio       = m["bio"] as? String ?: ""
            )
            try { trySend(state) } catch (_: Throwable) {}
        }
        awaitClose { reg.remove() }
    }

    /** Lectura única. */
    suspend fun getOnce(): ProfileFormState {
        val snap = doc.get().await()
        val m = snap.data.orEmpty()
        return ProfileFormState(
            nombre    = m["nombre"] as? String ?: "Nombre",
            apellido  = m["apellido"] as? String ?: "Apellido",
            email     = m["email"] as? String ?: "correo@dominio.com",
            telefono  = m["telefono"] as? String ?: "",
            ubicacion = m["ubicacion"] as? String ?: "Bogotá",
            bio       = m["bio"] as? String ?: ""
        )
    }

    /** Guarda todo el estado con merge. */
    suspend fun save(s: ProfileFormState) {
        val map = mapOf(
            "nombre" to s.nombre,
            "apellido" to s.apellido,
            "email" to s.email,
            "telefono" to s.telefono,
            "ubicacion" to s.ubicacion,
            "bio" to s.bio
        )
        doc.set(map, SetOptions.merge()).await()
    }
}
