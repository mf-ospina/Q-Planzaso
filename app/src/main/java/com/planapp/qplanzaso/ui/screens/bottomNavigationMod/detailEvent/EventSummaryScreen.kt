package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.planapp.qplanzaso.model.EventFormData
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.CategorySection
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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


    // Cargar imagen local
    LaunchedEffect(formData.imagenUri) {
        formData.imagenUri?.let { uri ->
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            imagenPreview = BitmapFactory.decodeStream(inputStream)
        }
    }

    var mostrarPopup by remember { mutableStateOf(false) }

    if (mostrarPopup) {
        AlertDialog(
            onDismissRequest = { mostrarPopup = false },
            confirmButton = {
                Button(
                    onClick = {
                        //Limpiar el formulario tras guardar el evento
                        viewModel.clearForm()
                        mostrarPopup = false
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Aceptar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Éxito",
                        tint = PrimaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "¡Evento publicado!",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        fontSize = 20.sp
                    )
                }
            },
            text = {
                Text(
                    text = "Tu evento ha sido publicado con éxito",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            tonalElevation = 8.dp
        )
    }


    val location = formData.ubicacion?.let { LatLng(it.latitude, it.longitude) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(color = Color.White) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val evento = Evento(
                                    nombre = formData.nombre,
                                    descripcion = formData.descripcion,
                                    categoriasIds = formData.categoriaId,
                                    fechaInicio = formData.fechaInicio,
                                    fechaFin = formData.fechaFin,
                                    precio = formData.precio,
                                    patrocinadores = formData.patrocinadores,
                                    direccion = formData.direccion,
                                    ubicacion = formData.ubicacion,
                                    organizadorId = formData.organizadorId,
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
                                        mostrarPopup = true
                                    },
                                    onError = { errorMsg -> mensaje = "Error: $errorMsg" }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text(
                            text = "Publicar evento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (mensaje.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // TopBar
            QTopBar(navController = navController, title = "Resumen del evento")

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ------------------- TÍTULO -------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formData.nombre,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkGrayText
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // ------------------- FECHA Y UBICACIÓN -------------------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Fecha
                    val fechaTexto = formData.fechaInicio?.let {
                        try {
                            val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
                            sdf.format(it.toDate())
                        } catch (e: Exception) {
                            "Fecha no válida"
                        }
                    } ?: "Fecha no especificada"

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Fecha",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = fechaTexto,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Dirección
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lugar",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = formData.direccion ?: "Ubicación no especificada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // ------------------- IMAGEN -------------------
                imagenPreview?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagen del evento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(1.dp))

                // ------------------- CATEGORÍAS -------------------
                Text(text = "Categorías", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                CategorySection(categories = formData.categoriaNombre.map { it })

                Spacer(modifier = Modifier.height(2.dp))

                // ------------------- DESCRIPCIÓN -------------------
                Text(text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    text = formData.descripcion,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                // ------------------- HORARIOS -------------------
                Text(text = "Fecha del evento", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                val formatoFechaHora = remember {
                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "ES"))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Inicio
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Inicio",
                                tint = Color(0xFFFFBA74)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Inicio",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                                )
                                Text(
                                    formData.fechaInicio?.toDate()?.let { formatoFechaHora.format(it) }
                                        ?: "No especificado",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color(0xFF333333),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        // Fin
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Fin",
                                tint = Color(0xFFFFBA74)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Fin",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                                )
                                Text(
                                    formData.fechaFin?.toDate()?.let { formatoFechaHora.format(it) }
                                        ?: "No especificado",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color(0xFF333333),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // ------------------- PRECIO -------------------
                val precioDouble = when (val p = formData.precio) {
                    is String -> p.toDoubleOrNull() ?: 0.0
                    is Number -> p.toDouble()
                    else -> 0.0
                }

                val textoPrecio = if (precioDouble <= 0.0) {
                    "Gratis"
                } else {
                    NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                        maximumFractionDigits = 0
                    }.format(precioDouble)
                }

                Text(text = "Precio", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Precio",
                            tint = Color(0xFF4CAF50)
                        )
                        Column {
                            Text("Precio", style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray))
                            Text(
                                textoPrecio,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = if (textoPrecio == "Gratis") Color(0xFF4CAF50) else Color(0xFF333333),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                // ------------------- PATROCINADORES -------------------
                Text(text = "Patrocinadores", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                if (formData.patrocinadores.isNullOrEmpty()) {
                    Text(
                        text = "No hay patrocinadores registrados",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        formData.patrocinadores.forEach { sponsor ->
                            SponsorChip(text = sponsor)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                // ------------------- DIRECCIÓN Y MAPA -------------------
                Text(text = "Dirección", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    text = formData.direccion,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )

                location?.let {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(it, 15f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
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
                            Marker(state = MarkerState(it), title = "Ubicación del evento")
                        }
                    }
                }
            }
        }
    }
}
