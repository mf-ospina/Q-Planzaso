package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importamos MoreVert y Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.model.ComentarioEvento
import com.planapp.qplanzaso.ui.components.RatingBar
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// =================================================================
// 1. COMMENTS SECTION (Mejorado)
// =================================================================

@Composable
fun CommentsSection(
    eventoId: String,
    eventoViewModel: EventoViewModel,
    modifier: Modifier = Modifier
) {
    val comentarios by eventoViewModel.comentarios.collectAsState()
    val loading by eventoViewModel.loading.collectAsState()
    val currentUsuarioId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(eventoId) {
        eventoViewModel.cargarComentarios(eventoId)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Comentarios (${comentarios.size})",
            fontWeight = FontWeight.ExtraBold, // Más énfasis
            fontSize = 22.sp, // Tamaño ligeramente mayor
        )

        if (loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        } else if (comentarios.isEmpty()) {
            Text(
                "Aún no hay comentarios. ¡Sé el primero en calificar!",
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // Añadir padding superior a la lista para separarla del título si no hay scroll
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp) // Espacio más sutil entre ítems
        ) {
            items(comentarios, key = { it.id ?: it.fecha.toString() }) { comentario ->
                CommentItem(
                    comentario = comentario,
                    eventoId = eventoId,
                    eventoViewModel = eventoViewModel,
                    currentUsuarioId = currentUsuarioId
                )
                // Eliminamos el Divider del CommentItem para que el Card/Surface lo maneje
            }
        }
    }
}

// =================================================================
// 2. COMMENT ITEM (Con diseño moderno)
// =================================================================

@Composable
fun CommentItem(
    comentario: ComentarioEvento,
    eventoId: String,
    eventoViewModel: EventoViewModel,
    currentUsuarioId: String?
) {
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val isUserComment = comentario.usuarioId == currentUsuarioId

    // 💡 Usamos Surface para un fondo sutil y esquinas redondeadas
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = Color(0xFFF7F7F7), // Un gris muy claro para diferenciar
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // --- ENCABEZADO (Nombre + Menú/Rating) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre del usuario
                Text(
                    text = comentario.nombre ?: "Usuario Anónimo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )

                // 💡 Menú de Opciones y Calificación
                if (isUserComment) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp) // Reducir un poco el tamaño del target
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
                        }

                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Editar") }, onClick = { showMenu = false; showEditDialog = true })
                            Divider()
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = {
                                    showMenu = false
                                    if (comentario.id != null && currentUsuarioId != null) {
                                        eventoViewModel.eliminarComentario(eventoId, comentario.id, currentUsuarioId)
                                    }
                                }
                            )
                        }
                    }
                } else if (comentario.calificacion > 0) {
                    // Solo mostrar el rating si no hay menú y la calificación existe
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = comentario.calificacion.toString().take(3),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFC107)
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // --- Cuerpo del Comentario ---
            Spacer(Modifier.height(8.dp))

            Text(
                comentario.texto ?: "No dejó texto.",
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = if (comentario.texto.isNullOrBlank()) Color.Gray.copy(alpha = 0.7f) else Color.DarkGray
            )

            // --- Pie de Página (Fecha) ---
            Spacer(Modifier.height(8.dp))
            Text(
                text = formatTimestamp(comentario.fecha),
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }
    }

    // ------------------------------------
    // ✏️ Diálogo de Edición
    // ------------------------------------
    if (showEditDialog && comentario.id != null) {
        CommentEditDialog(
            eventoId = eventoId,
            comentario = comentario,
            eventoViewModel = eventoViewModel,
            onDismiss = { showEditDialog = false }
        )
    }
}


// =================================================================
// 3. DIÁLOGO DE EDICIÓN (Mejorado)
// =================================================================
@Composable
fun CommentEditDialog(
    eventoId: String,
    comentario: ComentarioEvento,
    eventoViewModel: EventoViewModel,
    onDismiss: () -> Unit
) {
    var editedText by remember { mutableStateOf(comentario.texto ?: "") }
    var editedRating by remember { mutableStateOf(comentario.calificacion.toInt()) }

    val currentUsuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Calificación", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

                // 1. RatingBar de Edición Centrado
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RatingBar(
                        initialRating = editedRating,
                        onRatingChanged = { editedRating = it }
                    )
                }

                // 2. Campo de Texto Mejorado
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Comentario (Opcional)") },
                    placeholder = { Text("¿Alguna opinión que añadir?") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    singleLine = false,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedComentario = comentario.copy(
                        texto = editedText,
                        calificacion = editedRating.toDouble(),
                    )

                    eventoViewModel.editarComentario(
                        eventoId = eventoId,
                        comentario = updatedComentario,
                        usuarioId = currentUsuarioId
                    )
                    onDismiss()
                },
                enabled = editedRating > 0,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// Función auxiliar para formatear la fecha
private fun formatTimestamp(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(it)
    } ?: "Fecha desconocida"
}