package com.planapp.qplanzaso.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/** Data class para transportar los datos del formulario  cuando se crea un vento y se ve la vista previa*/
@Parcelize
data class EventFormData(
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val sponsors: List<String>,
    val allowPayment: Boolean,
    val allowRegistration: Boolean,
    val imageUri: String?
) : Parcelable