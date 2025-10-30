package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp // 👈 Importa Timestamp
import com.planapp.qplanzaso.model.ComentarioEvento // 👈 Importa tu modelo

/**
 * Un modal (AlertDialog) para escribir un comentario y dar una calificación.
 */
@Composable
fun CommentModal(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    // Devuelve un objeto ComentarioEvento listo para el ViewModel
    onAddComment: (comentario: ComentarioEvento) -> Unit
) {
    // Estado interno para el texto y las estrellas del modal
    var commentText by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(0) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,

            // Título del Modal
            title = { Text("Añade tu opinión") },

            // Contenido (TextField y RatingBar)
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Deja un comentario:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Escribe tu comentario...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Califica el evento (opcional):",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Usamos el RatingBar que ya tienes
                    RatingBar(
                        rating = userRating,
                        onRatingChanged = { userRating = it }
                    )
                }
            },

            // Botón de Confirmar
            confirmButton = {
                TextButton(
                    onClick = {
                        // 1. Creamos el objeto ComentarioEvento
                        val newComment = ComentarioEvento(
                            // Asumimos que tu modelo tiene estos campos
                            texto = commentText.trim(),
                            calificacion = userRating.toDouble(),
                            fecha = Timestamp.now()
                            // El 'usuarioId' lo añade tu ViewModel con .copy()
                        )

                        // 2. Enviamos el comentario hacia arriba
                        onAddComment(newComment)

                        // 3. Limpiamos y cerramos el modal
                        commentText = ""
                        userRating = 0
                        onDismissRequest()
                    },
                    // Solo se puede enviar si el comentario no está vacío
                    enabled = commentText.isNotBlank()
                ) {
                    Text("Publicar")
                }
            },

            // Botón de Cancelar
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/* NOTA: Asumí que tu modelo ComentarioEvento se ve así.
¡Asegúrate de que coincida con tu definición real en 'model/ComentarioEvento.kt'!

data class ComentarioEvento(
    val id: String? = null,
    val usuarioId: String? = null,
    val texto: String? = null,
    val calificacion: Double = 0.0,
    val fecha: Timestamp? = null
)
*/