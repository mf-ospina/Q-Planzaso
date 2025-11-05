package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.EventFormData
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.theme.boxBackground
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    encodedJson: String?,
    viewModel: EventoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 Decodificar el evento recibido por la ruta (igual que ya haces)
    val evento: Evento? = remember(encodedJson) {
        encodedJson?.let {
            try {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                    .create()
                val fixedJson = it.replace("+", " ")
                gson.fromJson(fixedJson, Evento::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // categorias disponibles (del ViewModel)
    LaunchedEffect(Unit) { viewModel.cargarDatosIniciales() } // asegura categorías
    val categoriasDisponibles by viewModel.categorias.collectAsState(initial = emptyList())
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ------------------ Estados del formulario ------------------
    var nombre by remember { mutableStateOf(evento?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(evento?.descripcion ?: "") }
    var direccion by remember { mutableStateOf(evento?.direccion ?: "") }
    var precio by remember { mutableStateOf(evento?.precio?.toString() ?: "") }
    var fechaInicio by remember { mutableStateOf(evento?.fechaInicio ?: Timestamp.now()) }
    var fechaFin by remember { mutableStateOf(evento?.fechaFin ?: Timestamp.now()) }
    var categoriasSeleccionadas by remember { mutableStateOf(evento?.categoriasIds ?: emptyList()) }
    var patrocinadores by remember { mutableStateOf(evento?.patrocinadores ?: emptyList()) }
    var ubicacion by remember { mutableStateOf(evento?.ubicacion ?: GeoPoint(0.0, 0.0)) }
    var ubicacionLatLng by remember { mutableStateOf(evento?.ubicacion?.let { LatLng(it.latitude, it.longitude) }) }
    var direccionMapa by remember { mutableStateOf(evento?.direccion ?: "") }

    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val imagenUrlActual = evento?.imagenUrl ?: evento?.imagen ?: ""

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
    }


    // ------------------ Recuperar ubicación del selector ------------------
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val latitud by savedStateHandle?.getStateFlow<Double?>("latitudSeleccionada", null)?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val longitud by savedStateHandle?.getStateFlow<Double?>("longitudSeleccionada", null)?.collectAsState()
        ?: remember { mutableStateOf(null) }
    val direccionMapaSeleccionada by savedStateHandle?.getStateFlow<String?>("direccionSeleccionada", null)?.collectAsState()
        ?: remember { mutableStateOf(null) }

    LaunchedEffect(latitud, longitud, direccionMapaSeleccionada) {
        if (latitud != null && longitud != null) {
            ubicacion = GeoPoint(latitud!!, longitud!!)
            ubicacionLatLng = LatLng(latitud!!, longitud!!)
            direccionMapaSeleccionada?.let { direccion = it }
        }
    }

    // estado temporal para nombre de patrocinador antes de agregarlo
    var sponsorName by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QTopBar(navController = navController, title = "Editar evento")

            // Nombre
            Text("Nombre del evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = boxBackground,
                    unfocusedContainerColor = boxBackground,
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray
                )
            )

            // Descripción
            Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = boxBackground,
                    unfocusedContainerColor = boxBackground,
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray
                ),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Gray
                ),
                maxLines = Int.MAX_VALUE
            )

            // Categorías (usa tu SelectorDeCategorias ya existente)
            SelectorDeCategorias(
                categoriasDisponibles = categoriasDisponibles,
                categoriasSeleccionadas = categoriasDisponibles.filter { categoriasSeleccionadas.contains(it.id) },
                onCategoriasSeleccionadas = { seleccionadas ->
                    categoriasSeleccionadas = seleccionadas.map { it.id ?: "" }
                }
            )

            // Fecha inicio (usa CampoFechaYHora existente)
            Text("Fecha de inicio", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            CampoFechaYHora(
                fechaSeleccionada = fechaInicio,
                onFechaHoraSeleccionada = { fechaInicio = it ?: fechaInicio }
            )

            // Fecha fin
            Text("Fecha de fin", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            CampoFechaYHora(
                fechaSeleccionada = fechaFin,
                onFechaHoraSeleccionada = { fechaFin = it ?: fechaFin },
                fechaInicioReferencia = fechaInicio
            )

            // Precio
            Text("Precio (opcional)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = precio,
                onValueChange = { input ->
                    if (input.matches(Regex("^\\d*([.,]?\\d*)?$"))) {
                        precio = input
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = { Text("$", color = Color.Gray, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = boxBackground,
                    unfocusedContainerColor = boxBackground,
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray
                )
            )

            // Patrocinadores
            Text("Patrocinadores (opcional)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = sponsorName,
                onValueChange = { sponsorName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej. Coca-Cola, Red Bull...") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (sponsorName.isNotBlank()) {
                            patrocinadores = patrocinadores + sponsorName.trim()
                            sponsorName = ""
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar patrocinador")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = boxBackground,
                    unfocusedContainerColor = boxBackground,
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray
                )
            )

            if (patrocinadores.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(patrocinadores.size) { index ->
                        SponsorTag(name = patrocinadores[index]) {
                            patrocinadores = patrocinadores.filterIndexed { i, _ -> i != index }
                        }
                    }
                }
            }

            // ---------------- DIRECCIÓN Y MAPA ----------------
            Text("Dirección (nombre o referencia del lugar)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej. Parque Simón Bolívar, Bogotá") },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedContainerColor = Color(0xFFF9F9F9)
                )
            )

            ubicacionLatLng?.let { location ->
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                }
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            scrollGesturesEnabled = false
                        )
                    ) {
                        Marker(state = MarkerState(location), title = "Ubicación seleccionada")
                    }
                }
            }

            direccionMapa.takeIf { it.isNotBlank() }?.let {
                Text("Dirección detectada: $it", color = Color.Gray, fontSize = 14.sp)
            }

            Button(
                onClick = { navController.navigate("selector_ubicacion") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundColor)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Seleccionar ubicación en mapa", color = Color.White)
            }

            // Imagen del evento (muestra imagen actual o nueva)
            Text("Imagen del evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)

            ImageUploadBox(
                imageUri = imagenUri,
                onImageSelected = { uri -> imagenUri = uri },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            // Si no se seleccionó nueva imagen, mostrar la actual
            if (imagenUri == null && imagenUrlActual.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(imagenUrlActual),
                    contentDescription = "Imagen actual",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Botón Guardar cambios
            val camposCompletos = nombre.isNotBlank() && descripcion.isNotBlank() && categoriasSeleccionadas.isNotEmpty()
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val idEvento = evento?.id
                            if (idEvento.isNullOrEmpty()) {
                                Toast.makeText(context, "ID de evento inválido", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val precioDouble = precio.replace(",", ".").toDoubleOrNull() ?: 0.0

                            val formData = EventFormData(
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                direccion = direccion.trim(),
                                precio = precioDouble,
                                patrocinadores = patrocinadores,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin,
                                categoriaId = categoriasSeleccionadas,
                                categoriaNombre = List(categoriasSeleccionadas.size) { "" }, // puedes mapear nombres si lo deseas
                                vibras = evento?.vibras ?: emptyList(),
                                organizadorId = evento?.organizadorId ?: "",
                                ubicacion = ubicacion,
                                imagenUri = imagenUri
                            )

                            viewModel.actualizarEvento(
                                eventoId = idEvento,
                                formData = formData,
                                onSuccess = {
                                    Toast.makeText(context, "Evento actualizado", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { msg ->
                                    Toast.makeText(context, "Error: $msg", Toast.LENGTH_LONG).show()
                                }
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = camposCompletos,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }

            // Mostrar mensaje de carga / error
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            error?.let {
                Text(text = it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
