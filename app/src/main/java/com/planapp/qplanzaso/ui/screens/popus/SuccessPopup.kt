package com.planapp.qplanzaso.ui.components // Puedes ponerlo en tu carpeta de 'components' o 'dialogs'

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SuccessPopup(
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit, // Acción a realizar cuando se presiona el botón
    onDismissRequest: () -> Unit // Acción cuando se intenta cerrar fuera del botón (ej. back press)
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), // Padding horizontal para que no ocupe todo el ancho
            shape = RoundedCornerShape(16.dp), // Bordes redondeados de la tarjeta
            colors = CardDefaults.cardColors(containerColor = Color.White), // Fondo blanco
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp), // Padding interno de la columna
                horizontalAlignment = Alignment.CenterHorizontally // Centrar contenido
            ) {
                // Mensaje del popup
                Text(
                    text = message,
                    fontSize = 24.sp, // Tamaño de fuente grande
                    fontWeight = FontWeight.Normal, // Peso normal, no negrita
                    color = Color.DarkGray, // Color de texto oscuro
                    textAlign = TextAlign.Center, // Texto centrado
                    lineHeight = 32.sp // Espaciado entre líneas para mejor lectura
                )

                Spacer(modifier = Modifier.height(32.dp)) // Espacio entre el texto y el botón

                // Botón
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp), // Ancho completo y altura fija
                    shape = RoundedCornerShape(12.dp), // Bordes redondeados del botón
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7A00), // Color naranja del botón
                        contentColor = Color.White // Texto blanco
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // Sin sombra propia para el botón si la tarjeta ya la tiene
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- Preview para ver el diseño en Android Studio ---
@Preview(showBackground = true, widthDp = 320)
@Composable
fun SuccessPopupPreview() {
    MaterialTheme { // Envuelve en MaterialTheme para que los colores se resuelvan correctamente
        SuccessPopup(
            message = "Cuenta creada exitosamente.",
            buttonText = "Ir al login",
            onButtonClick = { /* Acción de prueba */ },
            onDismissRequest = { /* Acción de prueba */ }
        )
    }
}