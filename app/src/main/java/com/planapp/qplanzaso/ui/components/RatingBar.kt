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
import androidx.compose.ui.unit.Dp // <-- Importante
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Int = 0,
    maxStars: Int = 5,
    onRatingChanged: (Int) -> Unit = {},
    // --- NUEVOS PARÁMETROS ---
    modifier: Modifier = Modifier,
    starSize: Dp = 40.dp,
    starColor: Color = Color(0xFFFFC107),
    unselectedColor: Color = Color.LightGray,
    isReadOnly: Boolean = false     // Para deshabilitar el clic
) {
    Row(
        // Usamos el modifier que nos pasan
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxStars) { index ->
            val isSelected = index < rating

            val starColorAnim by animateColorAsState(
                targetValue = if (isSelected) starColor else unselectedColor,
                label = "starColor"
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                label = "starScale"
            )

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Estrella ${index + 1}",
                tint = starColorAnim,
                modifier = Modifier
                    .size((starSize.value * scale).dp)
                    .clickable(
                        enabled = !isReadOnly
                    ) {
                        onRatingChanged(index + 1)
                    }
            )
        }
    }
}

@Preview(showBackground = true, name = "RatingBar Grande (Default)")
@Composable
fun RatingBarPreview() {
    RatingBar(rating = 3)
}
