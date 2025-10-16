package com.planapp.qplanzaso.model

/**
 * Representa estadísticas asociadas a un evento
 * y es utilizada tanto por organizadores como por asistentes.
 */
data class EventoStats(
    val eventoId: String = "",
    val visualizaciones: Int = 0,
    val asistentesCount: Int = 0,
    val favoritosCount: Int = 0,
    val calificacionPromedio: Double = 0.0
)
