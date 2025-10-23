package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.annotation.SuppressLint
import android.location.Location
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.planapp.qplanzaso.utils.GeocodingUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission") // asumimos que el permiso ya fue solicitado antes de entrar aquí
@Composable
fun SelectorUbicacionMapa(
    navController: NavController
) {
    val context: Context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var direccionActual by remember { mutableStateOf<String?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.60971, -74.08175), 12f) // Bogotá por defecto
    }

    // Servicio de ubicación del dispositivo
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // 🔹 Función para centrar la cámara en la ubicación actual
    fun centrarEnUbicacionActual() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    scope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        markerPosition = latLng

                        // Obtener dirección legible usando lat/lon
                        direccionActual = GeocodingUtils.obtenerDireccionDesdeCoordenadas(context, latLng.latitude, latLng.longitude)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar ubicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón para centrar en la ubicación actual
                FloatingActionButton(
                    onClick = { centrarEnUbicacionActual() }
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
                }

                // Botón OK para confirmar selección y devolver datos
                if (markerPosition != null) {
                    FloatingActionButton(onClick = {
                        scope.launch {
                            val mp = markerPosition!!
                            // si no tenemos dirección calculada, consúltala con lat/lon
                            val dir = direccionActual ?: GeocodingUtils.obtenerDireccionDesdeCoordenadas(context, mp.latitude, mp.longitude)

                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("ubicacionSeleccionada", mp)

                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("direccionSeleccionada", dir)

                            navController.popBackStack()
                        }
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                markerPosition = latLng
                // obtener dirección usando lat/lon (no pasar LatLng)
                scope.launch {
                    direccionActual = GeocodingUtils.obtenerDireccionDesdeCoordenadas(context, latLng.latitude, latLng.longitude)
                }
            },
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            markerPosition?.let {
                Marker(state = MarkerState(it), title = direccionActual ?: "Ubicación seleccionada")
            }
        }
    }
}