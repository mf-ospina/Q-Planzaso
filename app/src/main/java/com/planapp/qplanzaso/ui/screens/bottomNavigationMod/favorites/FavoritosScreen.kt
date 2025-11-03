package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.favorites

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FavoritosScreen(
    navController: NavController,
    viewModel: EventoViewModel,
    usuarioId: String
) {
    val favoritos by viewModel.eventosFavoritos.collectAsState()
    val scope = rememberCoroutineScope()

    // 🔹 Cargar favoritos al iniciar
    LaunchedEffect(usuarioId) {
        viewModel.refrescarFavoritos(usuarioId)
    }

    // 🔹 Escuchar cambios globales de favoritos (sincronización en tiempo real)
    LaunchedEffect(Unit) {
        viewModel.favoritosSync.collect {
            viewModel.refrescarFavoritos(usuarioId)
        }
    }

    Scaffold(
        topBar = { QTopBar(navController, title = "Favoritos") },
        containerColor = Color.White
    ) { paddingValues ->

        if (favoritos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes eventos favoritos todavía ❤️",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritos) { evento ->
                    FavoritoItemCardAnimated(
                        evento = evento,
                        onClick = {
                            // 🔹 Navegar al detalle del evento
                            val gson = GsonBuilder().create()
                            val eventoJson = gson.toJson(evento)
                            val encodedJson = java.net.URLEncoder.encode(eventoJson, "UTF-8")
                            navController.navigate("detailEvent/$encodedJson")
                        },
                        onRemove = {
                            // 🔹 Eliminar de favoritos con animación suave
                            scope.launch {
                                viewModel.toggleFavorito(evento, usuarioId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritoItemCardAnimated(
    evento: Evento,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fechaFormateada = evento.fechaInicio?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(it)
    } ?: "Sin fecha"

    // 🔹 Elevación animada
    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(targetValue = if (isPressed) 12.dp else 4.dp, label = "cardElevation")

    // 🔹 Animación del corazón ❤️
    var isRemoving by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isRemoving) 0.6f else 1f,
        animationSpec = tween(durationMillis = 250),
        label = "heartScale"
    )
    val tintColor by animateColorAsState(
        targetValue = if (isRemoving) Color.LightGray else Color.Red,
        animationSpec = tween(durationMillis = 250),
        label = "heartColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .graphicsLayer {
                shadowElevation = elevation.value
                shape = RoundedCornerShape(12.dp)
                clip = true
            }
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Imagen del evento
            AsyncImage(
                model = evento.imagenUrl?.takeIf { it.isNotEmpty() } ?: evento.imagen,
                contentDescription = evento.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(Color.LightGray)
            )

            // Contenido del evento
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = evento.nombre,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // ❤️ Botón eliminar favorito
                    IconButton(
                        onClick = {
                            isRemoving = true
                            scope.launch {
                                delay(250) // ⏳ Espera animación
                                Toast.makeText(context, "Eliminado de favoritos ❤️", Toast.LENGTH_SHORT).show()
                                onRemove()
                                isRemoving = false
                            }
                        },
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Eliminar de favoritos",
                            tint = tintColor
                        )
                    }
                }

                Text(
                    text = evento.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "📅 $fechaFormateada",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }
    }
}
