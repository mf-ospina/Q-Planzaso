package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.planapp.qplanzaso.model.EventFormData

@Composable
fun EventSummaryScreen(navController: NavController) {
    // Recordar los datos para recomposiciones
    val eventData = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<EventFormData>("eventData")

    // Si no hay datos, mostrar mensaje
    if (eventData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay datos del evento")
        }
        return
    }

    // Mostrar los datos del evento
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Nombre: ${eventData.name}")
        Text("Fecha: ${eventData.date}")
        Text("Hora: ${eventData.time}")
        Text("Lugar: ${eventData.location}")
        Text("Descripción: ${eventData.description}")
        Text("Patrocinadores: ${eventData.sponsors.joinToString(", ")}")
        Text("Pago: ${eventData.allowPayment}")
        Text("Permitir inscripción: ${eventData.allowRegistration}")
        Text("Imagen: ${eventData.imageUri ?: "No seleccionada"}")

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { /* Guardar en Firebase */ }) {
            Text("Guardar en Firebase")
        }
    }
}

