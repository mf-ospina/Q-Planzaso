package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// --- 1. Modelo de datos y datos de ejemplo ---
data class EventDetail(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val description: String
)

//Lo siguiente son datos de ejemplo para simular el detalle de una vista
val sampleEventDetail = EventDetail(
    id = "1",
    title = "Concierto Filarmónico",
    date = "14 Oct, 2025",
    location = "Movistar Arena, Bogotá",
    description = "Disfruta de una noche mágica con la orquesta filarmónica interpretando piezas clásicas y contemporáneas. Un evento imperdible para los amantes de la buena música en un ambiente único."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEvent(navController: NavController, id: String?) {
    // Por ahora, usamos los datos de ejemplo.
    // En el futuro, usarías el 'id' para cargar los datos reales desde un ViewModel.
    val event = sampleEventDetail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Placeholder de la Imagen ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.LightGray) // El recuadro gris que simula la imagen
                )
            }

            // --- Contenido del Evento ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título y botón de favorito
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /* Acción de favorito */ }) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito")
                        }
                    }

                    // Fecha y Ubicación
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(event.date, style = MaterialTheme.typography.bodyLarge)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Lugar", modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(event.location, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    // Descripción
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Botón de Acción
                    Button(
                        onClick = { /* Acción de comprar */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Comprar Entradas")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailEventPreview() {
    MaterialTheme {
        // Usamos un NavController de prueba para la vista previa
        DetailEvent(navController = rememberNavController(), id = "1")
    }
}