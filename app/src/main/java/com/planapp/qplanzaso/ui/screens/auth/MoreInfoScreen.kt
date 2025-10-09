package com.planapp.qplanzaso.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.R

// TODO: Crear este ViewModel para manejar la lógica de guardar el perfil del organizador.
class OrganizerViewModel : ViewModel() {
    fun saveProfile(
        empresa: String,
        nit: String,
        numero: String,
        direccion: String,
        email: String,
        onResult: (Boolean) -> Unit
    ) {
        println("Guardando perfil: Empresa=$empresa, NIT=$nit")
        // Aquí iría la lógica para llamar a un OrganizerRepository
        // que guarde estos datos en una nueva colección en Firestore
        // o actualice el documento del usuario.
        // viewModelScope.launch {
        //   val success = organizerRepository.saveProfile(userId, profileData)
        //   onResult(success)
        // }
        onResult(true) // Simula éxito
    }
}

@Composable
fun MoreInfoScreen(navController: NavController, organizerViewModel: OrganizerViewModel) {
    var empresa by remember { mutableStateOf("") }
    var nit by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var acceptsDataPolicy by remember { mutableStateOf(false) }
    var acceptsTerms by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen de Avatar
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

        // Campos de texto
        InfoTextField(label = "Empresa", value = empresa, onValueChange = { empresa = it })
        Spacer(modifier = Modifier.height(16.dp))
        InfoTextField(label = "NIT", value = nit, onValueChange = { nit = it }, keyboardType = KeyboardType.Number)
        Spacer(modifier = Modifier.height(16.dp))
        InfoTextField(label = "Número", value = numero, onValueChange = { numero = it }, keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(16.dp))
        InfoTextField(label = "Dirección", value = direccion, onValueChange = { direccion = it })
        Spacer(modifier = Modifier.height(16.dp))
        InfoTextField(label = "Email", value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)

        Spacer(modifier = Modifier.height(24.dp))

        // Toggles de aceptación
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

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

        // Botón Continuar
        Button(
            onClick = {
                loading = true
                organizerViewModel.saveProfile(empresa, nit, numero, direccion, email) { success ->
                    if (success) {
                        // TODO: Ajusta la ruta a tu dashboard o pantalla principal
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        loading = false
                        // Manejar error
                    }
                }
            },
            enabled = !loading && acceptsDataPolicy && acceptsTerms,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF9A825), // Naranja/amarillo
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Composable reutilizable para los campos de texto
@Composable
private fun InfoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFFF9A825), // Naranja al seleccionar
            unfocusedIndicatorColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)
    )
}

// Composable reutilizable para las filas con Switch
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
                checkedTrackColor = Color(0xFFF9A825)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Gray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MoreInfoScreenPreview() {
    MoreInfoScreen(rememberNavController(), OrganizerViewModel())
}