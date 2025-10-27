    package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

    import android.graphics.BitmapFactory
    import android.net.Uri
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
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
    import com.google.firebase.Timestamp
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.GeoPoint
    import com.google.maps.android.compose.*
    import com.planapp.qplanzaso.model.EventFormData
    import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
    import com.planapp.qplanzaso.utils.GeocodingUtils
    import kotlinx.coroutines.launch
    import java.io.InputStream

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
        var nombre by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var categoriaSeleccionada by remember { mutableStateOf("") }
        var direccion by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }
        var vibras by remember { mutableStateOf(listOf<String>()) }
        var fechaInicio by remember { mutableStateOf(Timestamp.now()) }
        var fechaFin by remember { mutableStateOf(Timestamp.now()) }

        // Imagen
        var imagenUri by remember { mutableStateOf<Uri?>(null) }
        var imagenPreview by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagenUri = uri
            if (uri != null) {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                imagenPreview = BitmapFactory.decodeStream(inputStream)
            }
        }

        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        val latitud by savedStateHandle?.getStateFlow<Double?>("latitudSeleccionada", null)?.collectAsState() ?: remember { mutableStateOf(null) }
        val longitud by savedStateHandle?.getStateFlow<Double?>("longitudSeleccionada", null)?.collectAsState() ?: remember { mutableStateOf(null) }
        val direccionMapa by savedStateHandle?.getStateFlow<String?>("direccionSeleccionada", null)?.collectAsState() ?: remember { mutableStateOf(null) }

        val ubicacionLatLng = if (latitud != null && longitud != null) LatLng(latitud!!, longitud!!) else null

        LaunchedEffect(ubicacionLatLng, direccionMapa) {
            if (direccionMapa != null) {
                direccion = direccionMapa!!
            } else if (ubicacionLatLng != null) {
                direccion = "${ubicacionLatLng.latitude}, ${ubicacionLatLng.longitude}"
            }
        }


        LaunchedEffect(ubicacionLatLng) {
            ubicacionLatLng?.let { latLng ->
                direccion = "${latLng.latitude}, ${latLng.longitude}"
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Crear nuevo evento") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })

                // Categoría
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = categoriaSeleccionada,
                        onValueChange = {},
                        label = { Text("Categoría") },
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    categoriaSeleccionada = categoria.nombre
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección o coordenadas") })

                // Vista previa de mapa si hay ubicación
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

                Button(onClick = { navController.navigate("selector_ubicacion") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Seleccionar ubicación en mapa")
                }

                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (opcional)") })

                Button(onClick = { launcher.launch("image/*") }) { Text("Seleccionar imagen") }

                imagenPreview?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp))
                }

                Button(
                    onClick = {
                        scope.launch {
                            val categoria = categorias.find { it.nombre == categoriaSeleccionada }
                            val geoPoint = ubicacionLatLng?.let { GeoPoint(it.latitude, it.longitude) }

                            val formData = EventFormData(
                                nombre = nombre,
                                descripcion = descripcion,
                                categoriaId = categoria?.id ?: "",
                                categoriaNombre = categoria?.nombre ?: "",
                                vibras = vibras,
                                precio = precio.toDoubleOrNull() ?: 0.0,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin,
                                organizadorId = usuarioId,
                                direccion = direccion,
                                ubicacion = geoPoint,
                                imagenUri = imagenUri
                            )

                            navController.currentBackStackEntry?.savedStateHandle?.set("formData", formData)
                            navController.navigate("EventSummaryScreen")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar a vista previa")
                }
            }
        }
    }