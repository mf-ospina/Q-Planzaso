package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

/**
 * 🔹 Muestra una lista horizontal de categorías sin título.
 * @param categories Lista de nombres de categorías a mostrar.
 */
@Composable
fun CategorySection(
    categories: List<String>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp), //espacio entre categoria
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(categories) { category ->
            CategoryChip(text = category)
        }
    }
}


// Función para generar un color aleatorio
private fun randomColor(): Color {
    val red = Random.nextInt(100, 201)
    val green = Random.nextInt(100, 201)
    val blue = Random.nextInt(100, 201)
    return Color(red, green, blue)
}


// Chip visual para mostrar una categoría individual.

@Composable
private fun CategoryChip(text: String) {
    val color = randomColor() // Asigna un color aleatorio
    Box(
        modifier = Modifier
            .background(color, shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

