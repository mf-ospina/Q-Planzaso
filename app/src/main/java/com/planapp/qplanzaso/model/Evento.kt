package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Evento(
    var id: String? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val vibra: String = "", // Ej: "chill", "cultural", "romántico"
    val fechaInicio: Timestamp? = null,
    val fechaFin: Timestamp? = null,
    val ubicacion: GeoPoint? = null,
    val direccion: String = "",
    val precio: Double = 0.0,
    val organizadorId: String = "",
    val imagenPortada: String = "",
    val estado: String = "proximo", // "proximo", "pasado", "cancelado"
    val verificado: Boolean = false,
    val calificacionPromedio: Double = 0.0,
    val asistentesCount: Int = 0,
    val favoritosCount: Int = 0
)