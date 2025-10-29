package com.planapp.qplanzaso.ui.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.planapp.qplanzaso.model.Evento
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventItem(evento: Evento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // viñeta color (aleatorio fijo por evento id para que sea consistente)
            val color = pickColorForEvent(evento.id)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = evento.nombre, style = MaterialTheme.typography.titleMedium)
                val formatterTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val inicio = evento.fechaInicio?.toDate()
                val fin = evento.fechaFin?.toDate()
                val horaTexto = if (inicio != null && fin != null) {
                    "${formatterTime.format(inicio)} - ${formatterTime.format(fin)}"
                } else if (inicio != null) {
                    formatterTime.format(inicio)
                } else {
                    "Hora no definida"
                }
                Text(text = horaTexto, style = MaterialTheme.typography.bodyMedium)
                if (!evento.direccion.isNullOrBlank()) {
                    Text(text = evento.direccion ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

private fun pickColorForEvent(id: String?): Color {
    // Generar color simple a partir del hash para consistencia
    val colors = listOf(
        Color(0xFF6A1B9A),
        Color(0xFF00897B),
        Color(0xFFEF6C00),
        Color(0xFF283593)
    )
    if (id.isNullOrBlank()) return colors[0]
    val idx = (id.hashCode().absoluteValue) % colors.size
    return colors[idx]
}

private val Int.absoluteValue: Int get() = if (this < 0) -this else this
