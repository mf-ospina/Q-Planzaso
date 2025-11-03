package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.model.ComentarioEvento
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel

/**
 * Sección que carga y muestra la lista de comentarios con paginación.
 */
@Composable
fun CommentSection(
    eventoId: String,
    viewModel: EventoViewModel,
    onStartEdit: (comentario: ComentarioEvento) -> Unit
) {
    val comentarios by viewModel.comentarios.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }

    LaunchedEffect(key1 = eventoId) {
        if (eventoId.isNotBlank()) {
            viewModel.cargarComentarios(eventoId)
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Text(
            stringResource(R.string.comments_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading && comentarios.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            comentarios.isEmpty() -> {
                Text(
                    stringResource(R.string.comments_empty_message),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comentarios) { comentario ->
                        CommentItem(
                            comentario = comentario,
                            isOwner = currentUserId != null && comentario.usuarioId == currentUserId,
                            onDelete = {
                                currentUserId?.let {
                                    viewModel.eliminarComentario(
                                        eventoId = eventoId,
                                        comentarioId = comentario.id,
                                        usuarioId = it
                                    )
                                }
                            },
                            onEdit = {
                                onStartEdit(comentario)
                            }
                        )
                    }

                    item {
                        Button(
                            onClick = { viewModel.cargarMasComentarios(eventoId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            Text(stringResource(R.string.comments_load_more))
                        }
                    }
                }
            }
        }
    }
}
