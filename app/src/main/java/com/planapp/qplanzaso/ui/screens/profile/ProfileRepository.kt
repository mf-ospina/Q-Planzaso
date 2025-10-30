package com.planapp.qplanzaso.ui.screens.profile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val uid: String,
    private val db: FirebaseFirestore
) {
    private fun docRef() = db.collection("usuarios").document(uid)
    suspend fun deleteUserDoc() {
        docRef().delete().await()
    }

    private fun Map<String, Any?>.toProfile(): ProfileFormState =
        ProfileFormState(
            nombre    = this["nombre"] as? String ?: "Nombre",
            correo    = this["correo"] as? String ?: "",
            telefono  = this["telefono"] as? String ?: "",
            ubicacion = this["ubicacion"] as? String ?: "Bogotá",
            bio       = this["bio"] as? String ?: ""
        )

    /** Flujo en tiempo real del documento de perfil */
    val data: Flow<ProfileFormState> = callbackFlow {
        val reg = docRef().addSnapshotListener { snap, err ->
            if (err != null) {
                // Cierra el flujo con error (o usa trySend con un fallback si prefieres no cerrar)
                close(err)
                return@addSnapshotListener
            }
            val map = snap?.data
            if (map != null) trySend(map.toProfile()).isSuccess
        }
        awaitClose { reg.remove() }
    }.distinctUntilChanged()

    /** Guardado no destructivo (merge) */
    suspend fun save(s: ProfileFormState) {
        val map = mapOf(
            "nombre" to s.nombre,
            "correo" to s.correo,
            "telefono" to s.telefono,
            "ubicacion" to s.ubicacion,
            "bio" to s.bio
        )
        docRef().set(map, SetOptions.merge()).await()
    }

    /** Carga one-shot (opcional, por si quieres prefetch sin listener) */
    suspend fun loadOnce(): ProfileFormState {
        val snap = docRef().get().await()
        return (snap.data ?: emptyMap<String, Any?>()).toProfile()
    }
    suspend fun deleteUserSubcollections() {
        // Ejemplo: "preferencias" y "dispositivos"
        val prefs = docRef().collection("preferencias").get().await()
        val batch = db.batch()
        prefs.documents.forEach { batch.delete(it.reference) }
        // Repite para otras subcolecciones:
        val disp = docRef().collection("dispositivos").get().await()
        disp.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }
}
