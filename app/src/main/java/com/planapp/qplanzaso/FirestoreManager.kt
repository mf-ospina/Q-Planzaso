package com.planapp.qplanzaso

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import android.util.Log

object FirestoreManager {

    private val db = FirebaseFirestore.getInstance()

    // -------------------------
    // Función para crear usuario
    // -------------------------
    fun crearUsuario(
        uid: String,
        nombre: String,
        correo: String,
        rol: String = "asistente",
        tipoUsuario: String = "particular",
        preferencias: List<String> = listOf()
    ) {
        val usuario = hashMapOf(
            "nombre" to nombre,
            "correo" to correo,
            "rol" to rol,
            "tipoUsuario" to tipoUsuario,
            "fechaRegistro" to FieldValue.serverTimestamp(),
            "preferencias" to preferencias,
            "fotoPerfil" to ""
        )

        db.collection("usuarios").document(uid)
            .set(usuario, SetOptions.merge())
            .addOnSuccessListener { Log.d("Firestore", "Usuario creado ✅") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error creando usuario ❌", e) }

        // Crear subcolecciones opcionales vacías
        db.collection("usuarios").document(uid).collection("eventosFavoritos")
        db.collection("usuarios").document(uid).collection("eventosInscritos")
    }

    // -------------------------
    // Función para crear evento
    // -------------------------
    fun crearEvento(
        nombre: String,
        descripcion: String,
        fechaInicio: String,
        fechaFin: String,
        ubicacion: Any, // GeoPoint
        categoria: String,
        precio: Double,
        organizadorId: String,
        imagenPortada: String = "",
        estado: String = "proximo"
    ): String {
        val evento = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "fechaInicio" to fechaInicio,
            "fechaFin" to fechaFin,
            "ubicacion" to ubicacion,
            "categoria" to categoria,
            "precio" to precio,
            "organizadorId" to organizadorId,
            "imagenPortada" to imagenPortada,
            "estado" to estado
        )

        val docRef = db.collection("eventos").document()
        docRef.set(evento)
            .addOnSuccessListener { Log.d("Firestore", "Evento creado ✅ ID: ${docRef.id}") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error creando evento ❌", e) }

        // Crear subcolecciones opcionales
        docRef.collection("asistentes")
        docRef.collection("favoritos")

        return docRef.id
    }

    // -------------------------
    // Crear notificación
    // -------------------------
    fun crearNotificacion(
        usuarioId: String,
        titulo: String,
        mensaje: String,
        tipo: String = "alerta"
    ) {
        val notif = hashMapOf(
            "usuarioId" to usuarioId,
            "titulo" to titulo,
            "mensaje" to mensaje,
            "tipo" to tipo,
            "fechaEnvio" to FieldValue.serverTimestamp(),
            "leida" to false
        )

        db.collection("notificaciones").add(notif)
            .addOnSuccessListener { Log.d("Firestore", "Notificación creada ✅") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error creando notificación ❌", e) }
    }

    // -------------------------
    // Crear recomendaciones IA
    // -------------------------
    fun crearRecomendacionesIA(usuarioId: String, eventosRecomendados: List<String>) {
        val reco = hashMapOf(
            "usuarioId" to usuarioId,
            "eventosRecomendados" to eventosRecomendados,
            "fechaGeneracion" to FieldValue.serverTimestamp()
        )

        db.collection("recomendacionesIA").add(reco)
            .addOnSuccessListener { Log.d("Firestore", "Recomendaciones IA creadas ✅") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error creando recomendaciones ❌", e) }
    }
}
