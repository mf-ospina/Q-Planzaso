package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    encodedJson: String?,
    viewModel:  EventoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 Decodificar el evento desde el JSON recibido
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

    // 🔹 Campos editables (prellenados si hay evento)
    var nombre by remember { mutableStateOf(evento?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(evento?.descripcion ?: "") }
    var direccion by remember { mutableStateOf(evento?.direccion ?: "") }
    var precio by remember { mutableStateOf(evento?.precio?.toString() ?: "") }
    var patrocinadores by remember { mutableStateOf(evento?.patrocinadores ?: emptyList()) }
    var fechaInicio by remember { mutableStateOf(evento?.fechaInicio ?: Timestamp.now()) }
    var fechaFin by remember { mutableStateOf(evento?.fechaFin ?: Timestamp.now()) }
    var categoriasSeleccionadas by remember { mutableStateOf(evento?.categoriasIds ?: emptyList()) }
    var ubicacion by remember { mutableStateOf(evento?.ubicacion ?: GeoPoint(0.0, 0.0)) }

    Scaffold(
        topBar = {
            // TopBar
            QTopBar(navController = navController, title = "Editar evento")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del evento") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🔹 Fechas visibles (solo lectura aquí)
            Text("📅 Fecha inicio: ${fechaInicio.toDate()}")
            Text("📅 Fecha fin: ${fechaFin.toDate()}")

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val idEvento = evento?.id ?: return@launch Toast.makeText(
                                context,
                                "Error: el ID del evento no es válido",
                                Toast.LENGTH_LONG
                            ).show()

                            val eventoActualizado = Evento(
                                id = idEvento,
                                nombre = nombre,
                                descripcion = descripcion,
                                direccion = direccion,
                                precio = precio.toDoubleOrNull() ?: 0.0,
                                patrocinadores = patrocinadores,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin,
                                categoriasIds = categoriasSeleccionadas,
                                ubicacion = ubicacion
                            )

                            // 🔹 Ahora el ID es seguro (no nulo)
                            viewModel.actualizarEvento(idEvento, eventoActualizado)

                            Toast.makeText(context, "Evento actualizado con éxito", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}