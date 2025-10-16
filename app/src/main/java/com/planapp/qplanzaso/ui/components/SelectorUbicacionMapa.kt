package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun SelectorUbicacionMapa(
    ubicacionActual: LatLng? = null,
    onUbicacionSeleccionada: (LatLng) -> Unit
) {
    // Estado de la cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ubicacionActual ?: LatLng(4.65, -74.05), 13f)
    }

    var marcadorPos by remember { mutableStateOf(ubicacionActual) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            marcadorPos = latLng
            onUbicacionSeleccionada(latLng)
        }
    ) {
        marcadorPos?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Ubicación seleccionada"
            )
        }
    }
}
