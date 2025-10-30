package com.planapp.qplanzaso.ui.components

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoriteButton(evento: Evento?, eventoViewModel: EventoViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser
    val usuarioId = user?.uid

    var esFavorito by remember { mutableStateOf(evento?.esFavorito ?: false) }

    val scale = animateFloatAsState(targetValue = if (esFavorito) 1.2f else 1f)
    val tintColor by animateColorAsState(targetValue = if (esFavorito) Color.Red else Color.LightGray)

    // --- Verifica si el evento es favorito al cargar ---
    LaunchedEffect(evento?.id, usuarioId) {
        if (evento != null && usuarioId != null) {
            try {
                eventoViewModel.actualizarEstadoFavoritoSeleccionado(usuarioId)
                esFavorito = eventoViewModel.eventoSeleccionado.value?.esFavorito ?: false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Botón interactivo ---
    IconButton(
        onClick = {
            if (usuarioId == null) {
                Toast.makeText(context, "Debes iniciar sesión para usar favoritos", Toast.LENGTH_SHORT).show()
                return@IconButton
            }

            if (evento == null) {
                Toast.makeText(context, "Evento no válido", Toast.LENGTH_SHORT).show()
                return@IconButton
            }

            coroutineScope.launch {
                esFavorito = !esFavorito
                eventoViewModel.toggleFavorito(evento, usuarioId) // ✅ PASA EL OBJETO EVENTO COMPLETO
            }
        }
    ) {
        Icon(
            imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
            tint = tintColor,
            modifier = Modifier
                .size(30.dp)
                .scale(scale.value)
        )
    }
}
