package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.*
import com.planapp.qplanzaso.model.EventFormData
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.theme.boxBackground
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    navController: NavController,
    viewModel: EventoViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.cargarDatosIniciales()
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val categorias by viewModel.categorias.collectAsState(initial = emptyList())

    // Campos del formulario
    var nombre by viewModel::nombre
    var descripcion by viewModel::descripcion
    var categoriasSeleccionadas by viewModel::categoriasSeleccionadas
    var fechaInicio by viewModel::fechaInicio
    var fechaFin by viewModel::fechaFin
    var precio by viewModel::precio
    var patrocinadores by viewModel::patrocinadores
    var direccion by viewModel::direccion
    var imagenUri by viewModel::imagenUri
    var ubicacionLatLng by viewModel::ubicacionLatLng
    var direccionMapa by viewModel::direccionMapa


    // Ubicacion del mapa
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val latitud by savedStateHandle?.getStateFlow<Double?>("latitudSeleccionada", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }
    val longitud by savedStateHandle?.getStateFlow<Double?>("longitudSeleccionada", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }
    val direccionMapaSeleccionada by savedStateHandle?.getStateFlow<String?>("direccionSeleccionada", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    // Cuando llega info del mapa, actualiza el ViewModel (persistente)
    LaunchedEffect(latitud, longitud, direccionMapaSeleccionada) {
        if (latitud != null && longitud != null) {
            viewModel.ubicacionLatLng = LatLng(latitud!!, longitud!!)
            viewModel.direccionMapa = direccionMapaSeleccionada
        }
    }

    //Guardar organizador
    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // TopBar
            QTopBar(navController = navController, title = "Nuevo evento")

            Spacer(modifier = Modifier.height(4.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre del evento
                Text(
                    text = "Nombre del evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BackgroundColor,
                        unfocusedBorderColor = BackgroundColor,
                        focusedContainerColor = boxBackground,
                        unfocusedContainerColor = boxBackground,
                        focusedTextColor = Color.Gray,
                        unfocusedTextColor = Color.Gray
                    )
                )

                Spacer(Modifier.height(2.dp))

                // Descripcion
                Text(
                    text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )

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

                Spacer(Modifier.height(2.dp))

                //Categorias
                SelectorDeCategorias(
                    categoriasDisponibles = categorias,
                    categoriasSeleccionadas = categoriasSeleccionadas,
                    onCategoriasSeleccionadas = { seleccionadas ->
                        categoriasSeleccionadas = seleccionadas
                    }
                )

                Spacer(Modifier.height(2.dp))

                //Fecha de inicio
                Text(
                    text = "Fecha de inicio", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )
                CampoFechaYHora(
                    fechaSeleccionada = fechaInicio, // ✅ ahora pasa el valor actual
                    onFechaHoraSeleccionada = { fechaInicio = it }
                )

                Spacer(Modifier.height(2.dp))


                //Fecha de fin
                Text(
                    text = "Fecha de fin", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )
                CampoFechaYHora(
                    fechaSeleccionada = fechaFin,
                    onFechaHoraSeleccionada = { fechaFin = it },
                    fechaInicioReferencia = fechaInicio
                )

                Spacer(Modifier.height(2.dp))

                // Precio
                Text(
                    text = "Precio (opcional)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )

                OutlinedTextField(
                    value = precio,
                    onValueChange = { input ->
                        // Acepta cualquier número o decimal
                        if (input.matches(Regex("^\\d*([.,]?\\d*)?$"))) {
                            precio = input
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = {
                        Text(
                            text = "$",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BackgroundColor,
                        unfocusedBorderColor = BackgroundColor,
                        focusedContainerColor = boxBackground,
                        unfocusedContainerColor = boxBackground,
                        focusedTextColor = Color.Gray,
                        unfocusedTextColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )


                Spacer(Modifier.height(2.dp))

                // Patrocinadores
                Text(
                    text = "Patrocinadores (opcional)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )

                var sponsorName by remember { mutableStateOf("") } // Estado temporal para el texto actual

                OutlinedTextField(
                    value = sponsorName,
                    onValueChange = { sponsorName = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BackgroundColor,
                        unfocusedBorderColor = BackgroundColor,
                        focusedContainerColor = boxBackground,
                        unfocusedContainerColor = boxBackground,
                        focusedTextColor = Color.Gray,
                        unfocusedTextColor = Color.Gray
                    ),
                    placeholder = { Text("Ej. Coca-Cola, Red Bull...") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (sponsorName.isNotBlank()) {
                                    // Agrega el patrocinador a la lista en el ViewModel sin perder estado
                                    patrocinadores = patrocinadores + sponsorName.trim()
                                    sponsorName = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar patrocinador",
                                tint = Color(0xFF6B5B5B)
                            )
                        }
                    }
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

                Spacer(Modifier.height(2.dp))

                // Dirección manual
                Text(
                    text = "Dirección (nombre o referencia del lugar)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej. Parque Simón Bolívar, Bogotá") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BackgroundColor,
                        unfocusedBorderColor = BackgroundColor,
                        focusedContainerColor = boxBackground,
                        unfocusedContainerColor = boxBackground,
                        focusedTextColor = Color.Gray,
                        unfocusedTextColor = Color.Gray
                    )
                )

                Spacer(Modifier.height(2.dp))

                // Vista previa de mapa
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
                            uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false)
                        ) {
                            Marker(state = MarkerState(location), title = "Ubicación seleccionada")
                        }
                    }
                }

                // Dirección detectada (solo visual)
                direccionMapa?.let {
                    Text(
                        text = "Dirección detectada: $it",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                // Botón para seleccionar ubicación
                Button(
                    onClick = { navController.navigate("selector_ubicacion") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BackgroundColor)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Seleccionar ubicación en mapa", color = Color.White)
                }

                Spacer(Modifier.height(2.dp))

                // Imagen
                Text(
                    text = "Imagen del evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText
                )

                ImageUploadBox(
                    imageUri = imagenUri,
                    onImageSelected = { uri ->
                        imagenUri = uri
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                //validacion d ecampos
                val camposCompletos = nombre.isNotBlank()
                        && descripcion.isNotBlank()
                        && categoriasSeleccionadas.isNotEmpty()
                        && fechaInicio != null
                        && fechaFin != null
                        && direccion.isNotBlank()
                        && ubicacionLatLng != null
                        && imagenUri != null


                Button(
                    onClick = {
                        scope.launch {
                            // usa la función del ViewModel para crear formData (devuelve null si falta algo)
                            val formData: EventFormData? = viewModel.toFormData(usuarioId)
                            if (formData != null) {
                                navController.currentBackStackEntry?.savedStateHandle?.set("formData", formData)
                                navController.navigate("EventSummaryScreen")
                            } else {
                                // opcional: mostrar snackbar o Toast; aquí solo no navega
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = camposCompletos,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text("Continuar a vista previa", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

        }
    }
}

// Componente reutilizable para las cajas de fecha
@Composable
fun CampoFechaYHora(
    fechaSeleccionada: Timestamp?, // ✅ Nuevo parámetro que viene del ViewModel
    onFechaHoraSeleccionada: (Timestamp?) -> Unit,
    fechaInicioReferencia: Timestamp? = null // 🔹 parámetro opcional para validar fecha de fin
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Mostrar fecha/hora ya guardada (si existe)
    var selectedDate by remember(fechaSeleccionada) {
        mutableStateOf(
            fechaSeleccionada?.toDate()?.let {
                "%02d/%02d/%d".format(it.date, it.month + 1, it.year + 1900)
            } ?: ""
        )
    }

    var selectedTime by remember(fechaSeleccionada) {
        mutableStateOf(
            fechaSeleccionada?.toDate()?.let {
                "%02d:%02d".format(it.hours, it.minutes)
            } ?: ""
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "%02d/%02d/%d".format(dayOfMonth, month + 1, year)

            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    selectedTime = "%02d:%02d".format(hourOfDay, minute)

                    // construir timestamp final (fecha + hora)
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val timestamp = Timestamp(cal.time)

                    // 🔸 Validar si hay una fecha de inicio de referencia
                    if (fechaInicioReferencia != null && timestamp.seconds <= fechaInicioReferencia.seconds) {
                        errorMessage = "La fecha no puede ser anterior a la de inicio"
                        onFechaHoraSeleccionada(null)
                    } else {
                        errorMessage = null
                        onFechaHoraSeleccionada(timestamp)
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = calendar.timeInMillis // Evitar seleccionar fechas anteriores a hoy

    // --- UI ---
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .background(Color(0xFFFFBA74), RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedDate.isNotEmpty()) "$selectedDate" else "Fecha",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFFFBA74), RoundedCornerShape(8.dp))
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedDate.isNotEmpty()) "$selectedTime" else "Hora",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { datePickerDialog.show() }, modifier = Modifier.size(60.dp)) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = Color.Gray,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // 🔹 Mostrar mensaje de error debajo si aplica
        errorMessage?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SelectorDeCategorias(
    categoriasDisponibles: List<com.planapp.qplanzaso.model.Categoria>,
    categoriasSeleccionadas: List<com.planapp.qplanzaso.model.Categoria>,
    onCategoriasSeleccionadas: (List<com.planapp.qplanzaso.model.Categoria>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Categorías",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF4A4A4A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            Column {
                // 🔹 FlowRow: reemplaza LazyRow para que las categorías se distribuyan en varias líneas
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (categoriasSeleccionadas.isEmpty()) {
                        Text(
                            text = "Ninguna categoría seleccionada",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    } else {
                        categoriasSeleccionadas.forEach { categoria ->
                            ChipItemRemovible(
                                label = categoria.nombre,
                                onRemove = {
                                    val nuevas = categoriasSeleccionadas.toMutableList()
                                    nuevas.remove(categoria)
                                    onCategoriasSeleccionadas(nuevas)
                                }
                            )
                        }
                    }

                    // 🔹 Botón para abrir menú de categorías
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFFFBA74), RoundedCornerShape(20.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar categoría",
                            tint = Color.Gray
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                categoriasDisponibles.forEach { categoria ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                categoria.nombre,
                                color = Color(0xFF333333),
                                fontSize = 15.sp
                            )
                        },
                        onClick = {
                            val nuevas = categoriasSeleccionadas.toMutableList()
                            if (nuevas.none { it.id == categoria.id }) {
                                nuevas.add(categoria)
                                onCategoriasSeleccionadas(nuevas)
                            }
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ChipItemRemovible(label: String, onRemove: () -> Unit) {
    val randomColor = remember { randomColor() } // Mantiene color estable por chip
    Row(
        modifier = Modifier
            .background(randomColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Eliminar",
            tint = Color.White,
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}
private fun randomColor(): Color {
    val red = Random.nextInt(80, 200)
    val green = Random.nextInt(80, 200)
    val blue = Random.nextInt(80, 200)
    return Color(red, green, blue)
}


@Composable
fun ImageUploadBox(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(boxBackground, shape = RoundedCornerShape(12.dp))
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { onImageSelected(null) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Eliminar imagen",
                    tint = Color.White,
                    modifier = Modifier.rotate(45f)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar imagen",
                    tint = Color.Gray,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Subir imagen del evento",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

//Funcion para los patrocinadores
@Composable
fun SponsorTag(name: String, onRemove: () -> Unit) {
    Surface(
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(50),
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = name,
                color = Color(0xFF5F5F5F),
                fontSize = 14.sp
            )
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Close),
                    contentDescription = "Eliminar patrocinador",
                    tint = Color(0xFF5F5F5F),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
