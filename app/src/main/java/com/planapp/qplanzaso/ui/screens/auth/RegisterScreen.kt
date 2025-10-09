package com.planapp.qplanzaso.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.R // Asegúrate de que este import sea correcto

@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var repetirClave by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var acceptsDataPolicy by remember { mutableStateOf(false) }
    var acceptsTerms by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp)
            .verticalScroll(rememberScrollState()), // Permite scroll si el contenido no cabe
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "Queremos saber más de ti",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campos del formulario
        StyledTextField(label = "Nombre", value = nombre, onValueChange = { nombre = it })
        Spacer(modifier = Modifier.height(16.dp))
        StyledTextField(label = "Apellido", value = apellido, onValueChange = { apellido = it })
        Spacer(modifier = Modifier.height(16.dp))
        StyledTextField(label = "Usuario", value = usuario, onValueChange = { usuario = it })
        Spacer(modifier = Modifier.height(16.dp))
        StyledTextField(label = "Clave", value = clave, onValueChange = { clave = it }, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        StyledTextField(label = "Repetir contraseña", value = repetirClave, onValueChange = { repetirClave = it }, isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        StyledTextField(label = "Email", value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)

        Spacer(modifier = Modifier.height(24.dp))

        // Opciones de aceptación
        AcceptanceRow(
            text = "Acepto el uso de tratamiento de datos.",
            checked = acceptsDataPolicy,
            onCheckedChange = { acceptsDataPolicy = it }
        )
        AcceptanceRow(
            text = "Acepto políticas de uso.",
            checked = acceptsTerms,
            onCheckedChange = { acceptsTerms = it }
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo

        // Botón de continuar
        Button(
            onClick = {
                // TODO: Aquí va la lógica para registrar al usuario (llamar al ViewModel)
            },
            enabled = acceptsDataPolicy && acceptsTerms, // Se activa solo si acepta las políticas
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF9A825), // Naranja
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Componente reutilizable para los campos de texto con el estilo de la imagen
@Composable
private fun StyledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFFF9A825), // Línea naranja cuando está seleccionado
            unfocusedIndicatorColor = Color.LightGray,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.Gray
        ),
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType, imeAction = imeAction)
    )
}

// Componente reutilizable para las filas con el interruptor
@Composable
private fun AcceptanceRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFF9A825) // Naranja cuando está activo
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}

// Previsualización para ver el diseño en Android Studio
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(rememberNavController())
}