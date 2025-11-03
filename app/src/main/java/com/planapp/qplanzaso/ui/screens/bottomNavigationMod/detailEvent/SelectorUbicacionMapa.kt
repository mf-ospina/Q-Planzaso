package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkButton
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.theme.boxBackground
import java.util.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorUbicacionMapa(navController: NavController) {
    val context = LocalContext.current
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var direccionTexto by remember { mutableStateOf(TextFieldValue("")) }
    var direccionActual by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.7110, -74.0721), 10f) // Bogotá
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {

        // 🏠 Campo de texto de búsqueda
        OutlinedTextField(
            value = direccionTexto,
            onValueChange = { newValue -> direccionTexto = newValue },
            label = { Text("Buscar dirección") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkButton,
                unfocusedBorderColor = DarkButton,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔍 Botón para buscar dirección escrita
        Button(
            onClick = {
                val coords: LatLng? = obtenerCoordenadasDesdeDireccion(context, direccionTexto.text)
                if (coords != null) {
                    markerPosition = coords
                    direccionActual = direccionTexto.text
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(coords, 15f))
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Buscar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🗺️ Mapa interactivo
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng: LatLng ->
                    markerPosition = latLng
                    direccionActual = obtenerDireccionDesdeCoordenadas(context, latLng)
                }
            ) {
                markerPosition?.let { pos ->
                    Marker(
                        state = MarkerState(position = pos),
                        title = "Ubicación seleccionada"
                    )
                }
            }

            // ✅ Botón flotante de confirmación
            if (markerPosition != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val mp = markerPosition!!
                        navController.previousBackStackEntry?.savedStateHandle?.set("latitudSeleccionada", mp.latitude)
                        navController.previousBackStackEntry?.savedStateHandle?.set("longitudSeleccionada", mp.longitude)
                        navController.previousBackStackEntry?.savedStateHandle?.set("direccionSeleccionada", direccionActual)
                        navController.popBackStack()
                    },
                    icon = { Icon(Icons.Default.Check, contentDescription = "Confirmar") },
                    text = { Text("Confirmar ubicación") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

/**
 * Convierte una dirección (texto) en coordenadas LatLng
 */
fun obtenerCoordenadasDesdeDireccion(context: Context, direccion: String): LatLng? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val direcciones: List<Address>? = geocoder.getFromLocationName(direccion, 1)
        if (!direcciones.isNullOrEmpty()) {
            val ubicacion = direcciones[0]
            LatLng(ubicacion.latitude, ubicacion.longitude)
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Convierte coordenadas (LatLng) en una dirección legible
 */
fun obtenerDireccionDesdeCoordenadas(context: Context, latLng: LatLng): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val direcciones: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!direcciones.isNullOrEmpty()) {
            direcciones[0].getAddressLine(0) ?: "Dirección no disponible"
        } else {
            "Dirección no disponible"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Dirección no disponible"
    }
}
