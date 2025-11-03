package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp

data class Notificacion(
    var id: String = "",
    var usuarioId: String = "",
    var titulo: String = "",
    var mensaje: String = "",
    var fecha: Timestamp? = null,              // nullable para evitar errores
    var tipo: TipoNotificacion? = null,        // nullable para Firestore
    var leida: Boolean = false
)
