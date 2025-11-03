package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.notifications

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.model.Notificacion
import com.planapp.qplanzaso.model.TipoNotificacion
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.viewModel.NotificacionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificacionesScreen(
    navController: NavController,
    viewModel: NotificacionViewModel
) {
    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
    val notificaciones by viewModel.notificaciones.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(usuarioId) {
        usuarioId?.let { viewModel.escucharNotificacionesTiempoReal(it) }
    }

    Scaffold(
        topBar = { QTopBar(navController = navController, title = "Notificaciones") },
        containerColor = Color.White
    ) { paddingValues ->

        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            notificaciones.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No tienes notificaciones por ahora 🎉",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filtradas = notificaciones.filter { noti ->
                        noti.tipo in listOf(
                            TipoNotificacion.EVENTO_MODIFICADO,
                            TipoNotificacion.FAVORITO_ELIMINADO,
                            TipoNotificacion.RECORDATORIO
                        )
                    }

                    items(filtradas, key = { it.id }) { notificacion ->
                        NotificacionItem(
                            notificacion = notificacion,
                            onClick = {
                                scope.launch { viewModel.marcarComoLeida(notificacion.id) }
                            },
                            onDelete = {
                                scope.launch { viewModel.eliminarNotificacion(notificacion.id) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificacionItem(
    notificacion: Notificacion,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (notificacion.leida) Color(0xFFF5F5F5) else Color(0xFFFFF2E7),
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    val icono = when (notificacion.tipo) {
        TipoNotificacion.EVENTO_MODIFICADO -> Icons.Default.NotificationsActive
        TipoNotificacion.FAVORITO_ELIMINADO -> Icons.Default.Delete
        TipoNotificacion.RECORDATORIO -> Icons.Default.NotificationsActive
        else -> Icons.Default.Notifications
    }

    val scale by animateFloatAsState(
        targetValue = if (notificacion.leida) 0.97f else 1f,
        animationSpec = tween(300),
        label = "scaleAnimation"
    )

    val fechaFormateada = remember(notificacion.fecha) {
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "ES"))
            notificacion.fecha?.toDate()?.let { sdf.format(it) } ?: "Fecha desconocida"
        } catch (e: Exception) {
            "Fecha desconocida"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Contenido de la notificación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = if (notificacion.leida) Color.Gray else Color(0xFFFF8C42),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = notificacion.titulo,
                        fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = notificacion.mensaje,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = fechaFormateada,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Papelera
            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFFF4D4D),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
