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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.model.ComentarioEvento
import androidx.compose.runtime.LaunchedEffect

/**
 * Un modal (AlertDialog) para escribir un comentario y dar una calificación.
 */
@Composable
fun CommentModal(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onAddComment: (comentario: ComentarioEvento) -> Unit,
    initialComentario: ComentarioEvento? = null
) {
    var commentText by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = showDialog) {
        if (showDialog && initialComentario != null) {
            commentText = initialComentario.texto
            userRating = initialComentario.calificacion.toInt()
        } else if (!showDialog) {
            commentText = ""
            userRating = 0
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    if (initialComentario != null)
                        stringResource(R.string.comment_edit_title)
                    else
                        stringResource(R.string.comment_add_title)
                )
            },

            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.comment_leave_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = {
                            Text(stringResource(R.string.comment_placeholder))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        stringResource(R.string.comment_rate_label),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    /*
                    RatingBar(

                        rating = userRating,
                        onRatingChanged = { userRating = it }
                    )

                     */
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        val newComment = ComentarioEvento(
                            texto = commentText.trim(),
                            calificacion = userRating.toDouble(),
                            fecha = Timestamp.now()
                        )
                        onAddComment(newComment)
                        commentText = ""
                        userRating = 0
                        onDismissRequest()
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Text(stringResource(R.string.comment_publish))
                }
            },

            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.comment_cancel))
                }
            }
        )
    }
}
