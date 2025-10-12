package com.planapp.qplanzaso.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val tipoUsuario: String = "particular", // "particular" o "empresarial"
    val fechaRegistro: Timestamp? = null,
    val preferencias: List<String> = listOf(),
    val fotoPerfil: String = "",
    val verified: Boolean = false,
    val eventosPublicados: Int = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "nombre" to nombre,
            "correo" to correo,
            "tipoUsuario" to tipoUsuario,
            "fechaRegistro" to fechaRegistro,
            "preferencias" to preferencias,
            "fotoPerfil" to fotoPerfil,
            "verified" to verified,
            "eventosPublicados" to eventosPublicados
        )
    }
}
