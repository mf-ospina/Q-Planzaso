package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Barra de búsqueda estándar y completamente editable.
 */
@Composable
fun SearchComponent(
    modifier: Modifier = Modifier,
    searchText: String, // El texto ahora es controlado por el padre
    onSearchTextChanged: (String) -> Unit, // Callback para actualizar el texto
    backgroundColor: Color = Color(0xFFE0E0E0),
    contentColor: Color = Color(0xFF757575),
    placeholderText: String = "Buscar planes..."
) {
    val shape = RoundedCornerShape(28.dp) // Usando un radio más grande, similar al de OutlinedTextField en Home.kt

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(color = backgroundColor, shape = shape)
            .border(width = 1.dp, color = backgroundColor, shape = shape)
            .padding(horizontal = 16.dp), // Ajustado padding
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChanged, // Llama directamente al callback del padre
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            cursorBrush = SolidColor(contentColor),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            // ⭐ NO USAMOS 'readOnly' AQUÍ: siempre es editable ⭐
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono de búsqueda (Izquierda)
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = contentColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Campo de texto y Placeholder
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = placeholderText,
                                color = contentColor,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    }

                    // Eliminamos el icono de filtro 'Tune'
                }
            }
        )
    }
}

// ----------------------------------------------------------------------
// PREVIEW
// ----------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun PreviewSearchComponentEditable() {
    // Para la vista previa, usamos un estado interno para simular cómo se usaría
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Modo editable (Estándar)")
        Spacer(Modifier.height(8.dp))
        SearchComponent(
            searchText = text,
            onSearchTextChanged = { text = it },
            placeholderText = "Escribe aquí para buscar..."
        )
    }
}