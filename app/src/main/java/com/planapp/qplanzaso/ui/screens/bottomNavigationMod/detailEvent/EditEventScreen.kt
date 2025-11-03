package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.model.EventFormData
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.boxBackground
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
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

    // 🔹 Decodificar el evento
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

    // 🔹 Campos editables
    var nombre by remember { mutableStateOf(evento?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(evento?.descripcion ?: "") }
    var direccion by remember { mutableStateOf(evento?.direccion ?: "") }
    var precio by remember { mutableStateOf(evento?.precio?.toString() ?: "") }
    var fechaInicio by remember { mutableStateOf(evento?.fechaInicio ?: Timestamp.now()) }
    var fechaFin by remember { mutableStateOf(evento?.fechaFin ?: Timestamp.now()) }
    var categoriasSeleccionadas by remember { mutableStateOf(evento?.categoriasIds ?: emptyList()) }
    var patrocinadores by remember { mutableStateOf(evento?.patrocinadores ?: emptyList()) }
    var ubicacion by remember { mutableStateOf(evento?.ubicacion ?: GeoPoint(0.0, 0.0)) }

    Scaffold(
        topBar = { QTopBar(navController = navController, title = "Editar evento") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = boxBackground,
                    unfocusedContainerColor = boxBackground,
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray
                ),
                maxLines = Int.MAX_VALUE
            )

            // Dirección
            Text("Dirección", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
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

            // Precio
            Text("Precio", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BackgroundColor,
                    unfocusedBorderColor = BackgroundColor,
                    focusedContainerColor = boxBackground,
                    unfocusedContainerColor = boxBackground,
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Fechas (solo visual)
            Text("📅 Fecha inicio: ${fechaInicio.toDate()}", color = Color.Gray)
            Text("📅 Fecha fin: ${fechaFin.toDate()}", color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para guardar cambios
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val idEvento = evento?.id
                            if (idEvento.isNullOrEmpty()) {
                                Toast.makeText(context, "ID del evento inválido", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            if (nombre.isBlank()) {
                                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val precioDouble = precio.toDoubleOrNull() ?: 0.0
                            val categoriasSafe = if (categoriasSeleccionadas.isEmpty()) emptyList() else categoriasSeleccionadas
                            val patrocinadoresSafe = if (patrocinadores.isEmpty()) emptyList() else patrocinadores

                            val formData = EventFormData(
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                direccion = direccion.trim(),
                                precio = precioDouble,
                                patrocinadores = patrocinadoresSafe,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin,
                                categoriaId = categoriasSafe,
                                categoriaNombre = categoriasSafe.map { "" },
                                vibras = emptyList(),
                                organizadorId = evento?.organizadorId ?: "",
                                ubicacion = ubicacion,
                                imagenUri = null
                            )

                            /*
                            viewModel.actualizarEvento(
                                eventoId = idEvento,
                                formData = formData,
                                onSuccess = {
                                    Toast.makeText(context, "Evento actualizado", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            )*/
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = Color.White)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}