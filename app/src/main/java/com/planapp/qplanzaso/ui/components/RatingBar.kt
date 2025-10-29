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
    // 1. Renombramos `initialRating` a `rating` para más claridad
    rating: Int = 0,
    maxStars: Int = 5,
    onRatingChanged: (Int) -> Unit = {}
) {
    // 2. ELIMINAMOS el estado interno.
    // var selectedRating by remember { mutableStateOf(initialRating.coerceIn(0, maxStars)) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxStars) { index ->
            // 3. Usamos el `rating` que nos pasa el padre
            val isSelected = index < rating

            val starColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFFFC107) else Color.LightGray,
                label = "starColor"
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                label = "starScale"
            )

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Estrella ${index + 1}",
                tint = starColor,
                modifier = Modifier
                    .size((40.dp.value * scale).dp)
                    .clickable {
                        // 4. Solo informamos al padre que se hizo clic
                        onRatingChanged(index + 1)
                    }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    // El Preview ahora necesita que le pasemos el rating
    RatingBar(rating = 3)
}