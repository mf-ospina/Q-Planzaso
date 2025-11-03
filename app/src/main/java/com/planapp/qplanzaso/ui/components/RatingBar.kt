package com.planapp.qplanzaso.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    initialRating: Int = 0,
    maxStars: Int = 5,
    onRatingChanged: (Int) -> Unit = {}
) {
    // 🔹 Estado que guarda la calificación seleccionada por el usuario
    // CoerceIn asegura que la calificación esté entre 0 y maxStars (usualmente 5)
    var selectedRating by remember { mutableStateOf(initialRating.coerceIn(0, maxStars)) }

    Row(
        modifier = Modifier
            .fillMaxWidth(), // Ocupar todo el ancho disponible
        horizontalArrangement = Arrangement.Center, // Centrar las estrellas
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔹 Repite el bloque de código para dibujar cada estrella
        repeat(maxStars) { index ->
            val starValue = index + 1
            val isSelected = starValue <= selectedRating // index < selectedRating

            // 🔹 Animación de color suave (amarillo brillante o gris claro)
            val starColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFFFC107) else Color.LightGray,
                label = "starColor"
            )
            // 🔹 Animación de escala (un pequeño "pop" al seleccionar)
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                label = "starScale"
            )

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Estrella $starValue",
                tint = starColor,
                modifier = Modifier
                    .size((40.dp.value * scale).dp) // Aplica el tamaño y la escala
                    .clickable {
                        // 🔹 Actualiza el estado local y notifica al componente padre
                        selectedRating = starValue
                        onRatingChanged(selectedRating)
                    }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    RatingBar(initialRating = 3)
}