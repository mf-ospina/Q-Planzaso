package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp

data class Notificacion(
    val id: String = "",
    val usuarioId: String = "",         // 👈 NUEVO
    val titulo: String = "",
    val mensaje: String = "",
    val tipo: String = "alerta",        // "alerta", "recordatorio", "social"
    val fechaEnvio: Timestamp? = null,
    val leida: Boolean = false
)
