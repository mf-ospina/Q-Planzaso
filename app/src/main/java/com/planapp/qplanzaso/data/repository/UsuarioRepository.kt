package com.planapp.qplanzaso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import com.planapp.qplanzaso.model.Usuario
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.model.RecomendacionIA
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    //Logica Firebase
    private val db = FirebaseFirestore.getInstance()

    //Logica de usuarios para el login
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

    // 🔹 Obtener lista de eventos favoritos
    suspend fun obtenerEventosFavoritos(uid: String): List<Evento> {
        val snapshot = db.collection("usuarios").document(uid)
            .collection("eventosFavoritos").get().await()
        return snapshot.toObjects(Evento::class.java)
    }

    // 🔹 Eliminar evento de favoritos
    suspend fun eliminarEventoFavorito(uid: String, eventoId: String) {
        db.collection("usuarios").document(uid)
            .collection("eventosFavoritos").document(eventoId).delete().await()
    }

    // 🔹 Obtener eventos inscritos
    suspend fun obtenerEventosInscritos(uid: String): List<Evento> {
        val snapshot = db.collection("usuarios").document(uid)
            .collection("eventosInscritos").get().await()
        return snapshot.toObjects(Evento::class.java)
    }

    // 🔹 Eliminar inscripción a evento
    suspend fun eliminarEventoInscrito(uid: String, eventoId: String) {
        db.collection("usuarios").document(uid)
            .collection("eventosInscritos").document(eventoId).delete().await()
    }

    // 🔹 Incrementar contador de eventos publicados por organizador
    suspend fun incrementarEventosPublicados(uid: String) {
        db.collection("usuarios").document(uid)
            .update("eventosPublicados", com.google.firebase.firestore.FieldValue.increment(1)).await()
    }

    // 🔹 Disminuir contador al eliminar evento
    suspend fun decrementarEventosPublicados(uid: String) {
        db.collection("usuarios").document(uid)
            .update("eventosPublicados", com.google.firebase.firestore.FieldValue.increment(-1)).await()
    }

}
