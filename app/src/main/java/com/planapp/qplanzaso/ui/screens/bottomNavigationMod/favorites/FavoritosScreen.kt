package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FavoritosScreen(
    viewModel: EventoViewModel,
    usuarioId: String,
    onEventoClick: (Evento) -> Unit = {}
) {
    val favoritos by viewModel.eventosFavoritos.collectAsState()

    // Carga inicial de favoritos
    LaunchedEffect(usuarioId) {
        viewModel.cargarEventosFavoritos(usuarioId)
    }

    if (favoritos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No tienes eventos favoritos todavía ❤️",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favoritos) { evento ->
                EventoFavoritoCard(evento = evento) {
                    onEventoClick(evento)
                }
            }
        }
    }
}

@Composable
fun EventoFavoritoCard(evento: Evento, onClick: () -> Unit) {
    val fechaFormateada = evento.fechaInicio?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
    } ?: "Sin fecha"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column {
            // Imagen del evento
            AsyncImage(
                model = evento.imagenUrl?.takeIf { it.isNotEmpty() } ?: evento.imagen,
                contentDescription = evento.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color.LightGray)
            )

            // Contenido textual
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = evento.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = evento.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "📅 $fechaFormateada",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }
    }
}
