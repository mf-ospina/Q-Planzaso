package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.model.ComentarioEvento
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import java.text.SimpleDateFormat
import java.util.*

/**
 * Muestra la UI para un único comentario.
 * Esta versión USA el 'nombreUsuario' del modelo.
 */
@Composable
fun CommentItem(
    comentario: ComentarioEvento,
    onEdit: () -> Unit,
    isOwner: Boolean,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre y Fecha
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(
                        text = comentario.nombre.ifEmpty { stringResource(R.string.comment_default_username) },
                        fontWeight = FontWeight.Bold,
                        color = DarkGrayText
                    )
                    Text(
                        text = formatTimestamp(comentario.fecha),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                if (isOwner) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.comment_edit_description),
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.comment_delete_description),
                            tint = Color.Gray,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (comentario.calificacion > 0) {
                RatingBar(
                    rating = comentario.calificacion.toInt(),
                    onRatingChanged = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Texto del comentario
            Text(
                text = comentario.texto,
                color = DarkGrayText,
                fontSize = 15.sp
            )
        }
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.comment_delete_title)) },
            text = { Text(stringResource(R.string.comment_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) { Text(stringResource(R.string.comment_delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.comment_delete_cancel))
                }
            }
        )
    }
}

@Composable
private fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return stringResource(R.string.comment_now)
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "ES"))
        sdf.format(timestamp.toDate())
    } catch (e: Exception) {
        stringResource(R.string.comment_invalid_date)
    }
}
