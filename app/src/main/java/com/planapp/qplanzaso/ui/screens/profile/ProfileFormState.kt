package com.planapp.qplanzaso.ui.screens.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileFormState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val ubicacion: String = "",
    val bio: String = ""
) : Parcelable