package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

/**
 * Representa un evento dentro de la aplicación.
 * Modelo compatible con Firestore y el módulo de calendario.
 */
data class Evento(
    var id: String? = null,
    var nombre: String = "",
    var descripcion: String = "",
    var categoriasIds: List<String> = emptyList(),
    var precio: Double? = 0.0,

    // 🔹 Fechas
    var fechaInicio: Timestamp? = null,
    var fechaFin: Timestamp? = null,

    // 🔹 Organización
    var organizadorId: String = "",
    var verificado: Boolean? = false,
    var estado: String = "proximo",

    // 🔹 Calificaciones / métricas
    var calificacionPromedio: Double? = 0.0,
    var calificacionesCount: Int? = 0,
    var asistentesCount: Int? = 0,
    var favoritosCount: Int? = 0,

    // 🔹 Imágenes
    var imagen: String? = null,
    var imagenUrl: String? = null,

    // 🔹 Ubicación
    var ubicacion: GeoPoint? = null,
    var direccion: String? = null,
    var ciudad: String? = null,
    var pais: String? = null,

    // 🔹 Inscripciones
    var inscritosIds: List<String> = emptyList(),

    // 🔹 Patrocinadores
    var patrocinadores: List<String> = emptyList()
)
