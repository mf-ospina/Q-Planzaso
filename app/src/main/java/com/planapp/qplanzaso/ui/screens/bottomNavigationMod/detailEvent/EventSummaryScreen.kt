package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.planapp.qplanzaso.model.EventFormData
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import kotlinx.coroutines.launch
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSummaryScreen(
    navController: NavController,
    viewModel: EventoViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formData = navController.previousBackStackEntry?.savedStateHandle?.get<EventFormData>("formData")
    if (formData == null) {
        Text("No hay datos del evento para mostrar")
        return
    }

    var mensaje by remember { mutableStateOf("") }

    var imagenPreview by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(formData.imagenUri) {
        formData.imagenUri?.let { uri ->
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            imagenPreview = BitmapFactory.decodeStream(inputStream)
        }
    }

    val location = formData.ubicacion?.let { LatLng(it.latitude, it.longitude) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vista previa del evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            val evento = Evento(
                                nombre = formData.nombre,
                                descripcion = formData.descripcion,
                                categoriasIds = listOf(formData.categoriaId),
                                precio = formData.precio,
                                fechaInicio = formData.fechaInicio,
                                fechaFin = formData.fechaFin,
                                organizadorId = formData.organizadorId,
                                ubicacion = formData.ubicacion,
                                direccion = formData.direccion,
                                imagenUrl = null
                            )

                            viewModel.crearEvento(
                                evento,
                                onSuccess = { eventoId ->
                                    if (formData.imagenUri != null) {
                                        viewModel.subirImagenEvento(formData.imagenUri, eventoId) { url ->
                                            println("✅ Imagen subida correctamente: $url")
                                        }
                                    }
                                    mensaje = "Evento publicado con éxito 🎉"
                                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                                },
                                onError = { errorMsg -> mensaje = "Error: $errorMsg" }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Publicar evento") }

                if (mensaje.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(mensaje, color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Resumen del evento", style = MaterialTheme.typography.titleLarge)
            imagenPreview?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "Imagen del evento", modifier = Modifier.fillMaxWidth().height(200.dp))
            }

            location?.let {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(it, 15f)
                }
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false)
                    ) {
                        Marker(state = MarkerState(it), title = "Ubicación del evento")
                    }
                }
            }

            Text("📛 ${formData.nombre}")
            Text("📝 ${formData.descripcion}")
            Text("🏷 ${formData.categoriaNombre}")
            Text("📍 ${formData.direccion}")
            Text("💰 $${formData.precio}")
        }
    }
}