package com.planapp.qplanzaso.model

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Data class que encapsula los datos temporales del formulario de creación de eventos.
 * Se usa para transportar la información hacia la vista de vista previa antes de publicarlo.
 */

@Parcelize
data class EventFormData(
    val nombre: String = "",
    val descripcion: String = "",
    val categoriaId: @RawValue List<String> = emptyList(),
    val categoriaNombre: @RawValue List<String> = emptyList(),
    val vibras: @RawValue List<String> = emptyList(),
    val precio: Double = 0.0,
    val patrocinadores: @RawValue List<String> = emptyList(),
    val fechaInicio: Timestamp = Timestamp.now(),
    val fechaFin: Timestamp = Timestamp.now(),
    val organizadorId: String = "",
    val direccion: String = "",
    val ubicacion: @RawValue GeoPoint? = null,
    val imagenUri: @RawValue Uri? = null
):Parcelable