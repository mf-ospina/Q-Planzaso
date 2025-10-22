package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.theme.boxBackground
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.planapp.qplanzaso.model.EventFormData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(navController: NavController) {
    var eventName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var sponsorName by remember { mutableStateOf("") }
    var sponsors by remember { mutableStateOf(listOf<String>()) }
    var allowPayment by remember { mutableStateOf(false) }
    var allowRegistration by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }  //para la imagen que se suba

    // Estados para la fecha
    var selectedDay by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }


    // Variables para guardar hora y minutos
    var selectedHour by remember { mutableStateOf("") }
    var selectedMinute by remember { mutableStateOf("") }
    var selectedAmPm by remember { mutableStateOf("") }

    // Calendario para seleccionar fecha
    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    // Dialogo fecha
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDay = dayOfMonth.toString()
            selectedMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.apply { set(Calendar.MONTH, month) }.time)
            selectedYear = year.toString()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = calendar.timeInMillis // Evitar seleccionar fechas anteriores a hoy



    // Diálogo de hora
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            selectedHour = hour.toString().padStart(2, '0')
            selectedMinute = minute.toString().padStart(2, '0')
            selectedAmPm = amPm
        },
        12, 0, false // Hora inicial (12:00 PM)
    )



    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(80.dp),
                containerColor = Color.White
            ) {
                Button(
                    onClick = {
                        // Validar campos antes de navegar
                        if (eventName.isBlank() || selectedDay.isBlank() || selectedMonth.isBlank() || selectedYear.isBlank()) {
                            // Mostrar toast o alerta
                            return@Button
                        }

                        val eventData = EventFormData(
                            name = eventName,
                            date = "$selectedDay $selectedMonth $selectedYear",
                            time = "$selectedHour:$selectedMinute $selectedAmPm",
                            location = location,
                            description = description,
                            sponsors = sponsors,
                            allowPayment = allowPayment,
                            allowRegistration = allowRegistration,
                            imageUri = selectedImageUri?.toString()
                        )

                        navController.currentBackStackEntry?.savedStateHandle?.set("eventData", eventData)
                        navController.navigate("EventSummaryScreen")
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "Siguiente",
                        fontSize = 19.sp
                    )
                }
            }
        }

    //Contenido principal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
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
                    text = "Nombre del evento",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
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

                Spacer(Modifier.height(2.dp))

                // Fecha
                Text(
                    text = "Fecha",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    // Día
                    DateBox(value = selectedDay.ifEmpty { "DD" })
                    // Mes
                    DateBox(value = selectedMonth.ifEmpty { "MM" })
                    // Año
                    DateBox(value = selectedYear.ifEmpty { "YYYY" })
                    // Icono de calendario
                    IconButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Seleccionar fecha",
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Lugar
                Text(
                    text = "Lugar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.weight(1f).height(50.dp),
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

                    IconButton(
                        onClick = {  },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "location",
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Imagen
                Text(
                    text = "Imagen del evento",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                ImageUploadBox(
                    imageUri = selectedImageUri,
                    onImageSelected = { uri -> selectedImageUri = uri }
                )

                Spacer(Modifier.height(2.dp))

                // Categorías
                Text(
                    text = "Categorias",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        ChipItem("Concierto", Color(0xFFFF69B4))
                        ChipItem("Teatro", Color(0xFF9370DB))
                        IconButton(onClick = { /* Agregar categoría */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar")
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Descripción
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
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

                //Organizadores
                Text(
                    text = "Organizadores",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                // Campo de texto
                OutlinedTextField(
                    value = sponsorName,
                    onValueChange = { sponsorName = it },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BackgroundColor,
                        unfocusedBorderColor = BackgroundColor,
                        focusedContainerColor = boxBackground,
                        unfocusedContainerColor = boxBackground,
                        focusedTextColor = Color.Gray,
                        unfocusedTextColor = Color.Gray
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (sponsorName.isNotBlank()) {
                                    sponsors = sponsors + sponsorName.trim()
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
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sponsors.size) { index ->
                        SponsorTag(name = sponsors[index]) {
                            sponsors = sponsors.filterIndexed { i, _ -> i != index }
                        }
                    }
                }


                Spacer(Modifier.height(8.dp))

                // hora
                Text(
                    text = "Horario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    // Hora
                    DateBox(value = selectedHour.ifEmpty { "hh" })
                    // Minuto
                    DateBox(value = selectedMinute.ifEmpty { "mm" })
                    // AM/PM
                    DateBox(value = selectedAmPm.ifEmpty { "AM/PM" })
                    // Ícono de reloj
                    IconButton(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Seleccionar hora",
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                //Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("El evento es de pago")
                    Switch(checked = allowPayment, onCheckedChange = { allowPayment = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Permitir inscripción al evento")
                    Switch(checked = allowRegistration, onCheckedChange = { allowRegistration = it })
                }

                Spacer(Modifier.height(10.dp))
            }


        }
    }
}

@Composable
fun ChipItem(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// Componente reutilizable para las cajas de fecha
@Composable
fun DateBox(value: String) {
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(40.dp)
            .background(Color(0xFFFFBA74), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// funcion para subir una imagen
@Composable
fun ImageUploadBox(
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            .clickable { launcher.launch("image/*") }, // abre galería
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            // Mostrar imagen seleccionada
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Botón pequeño para eliminar/reemplazar (opcional)
            IconButton(
                onClick = { onImageSelected(null) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // puedes usar otro ícono (p.ej. Delete)
                    contentDescription = "Eliminar imagen",
                    tint = Color.White,
                    modifier = Modifier.rotate(45f) // convierte + en x rápido (opcional)
                )
            }
        } else {
            // Estado vacío: icono + texto
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

