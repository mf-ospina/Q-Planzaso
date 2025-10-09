package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.planapp.qplanzaso.model.Usuario
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.model.RecomendacionIA
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun crearUsuario(usuario: Usuario) {
        db.collection("usuarios").document(usuario.uid)
            .set(usuario, SetOptions.merge()).await()
    }

    suspend fun obtenerUsuario(uid: String): Usuario? {
        val snapshot = db.collection("usuarios").document(uid).get().await()
        return snapshot.toObject(Usuario::class.java)
    }

    suspend fun actualizarPreferencias(uid: String, preferencias: List<String>) {
        db.collection("usuarios").document(uid)
            .update("preferencias", preferencias).await()
    }

    suspend fun eliminarUsuario(uid: String) {
        db.collection("usuarios").document(uid).delete().await()
    }

    // Subcolecciones
    suspend fun agregarEventoFavorito(uid: String, evento: Evento) {
        db.collection("usuarios").document(uid)
            .collection("eventosFavoritos").document(evento.id!!)
            .set(evento, SetOptions.merge()).await()
    }

    suspend fun agregarEventoInscrito(uid: String, evento: Evento) {
        db.collection("usuarios").document(uid)
            .collection("eventosInscritos").document(evento.id!!)
            .set(evento, SetOptions.merge()).await()
    }

    suspend fun guardarRecomendacion(uid: String, recomendacion: RecomendacionIA) {
        db.collection("usuarios").document(uid)
            .collection("recomendaciones").add(recomendacion).await()
    }
}
