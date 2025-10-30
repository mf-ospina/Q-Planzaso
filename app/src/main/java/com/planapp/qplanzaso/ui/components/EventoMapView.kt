package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun EventoMapView(
    lat: Double?,
    lon: Double?,
    nombreEvento: String,
    modifier: Modifier = Modifier
) {
    if (lat == null || lon == null) {
        // Si no hay coordenadas, no mostramos el mapa
        return
    }

    val eventoPos = LatLng(lat, lon)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(eventoPos, 15f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp)) // sombra suave
            .clip(RoundedCornerShape(20.dp))
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true
            )
        ) {
            Marker(
                state = MarkerState(position = eventoPos),
                title = nombreEvento,
                snippet = "Ubicación del evento"
            )
        }
    }
}
