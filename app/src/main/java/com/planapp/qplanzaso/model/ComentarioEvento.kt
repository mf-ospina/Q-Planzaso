package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp

data class ComentarioEvento(
    val id: String = "",
    val usuarioId: String = "",
    val nombreUsuario: String = "",
    val texto: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val calificacion: Double = 0.0 // opcional: si el usuario deja calificación junto al comentario
)
