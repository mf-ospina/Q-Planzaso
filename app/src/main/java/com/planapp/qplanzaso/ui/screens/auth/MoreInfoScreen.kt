package com.planapp.qplanzaso.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.auth.AuthViewModel

// ✅ ViewModel simple que guarda los datos del organizador
class OrganizerViewModel {
    private val db = FirebaseFirestore.getInstance()

    fun saveProfile(
        userId: String,
        empresa: String,
        nit: String,
        numero: String,
        direccion: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val organizerData = mapOf(
            "uid" to userId,
            "empresa" to empresa,
            "nit" to nit,
            "numero" to numero,
            "direccion" to direccion,
            "email" to email,
            "password" to password // ⚠️ Solo para pruebas. No guardes contraseñas reales.
        )

        db.collection("organizers").document(userId)
            .set(organizerData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}

@Composable
fun MoreInfoScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // ✅ Se crea el ViewModel localmente (opción 1)
    val organizerViewModel = remember { OrganizerViewModel() }
    val viewModel: AuthViewModel = viewModel() // Por si necesitas acceder al AuthViewModel

    var empresa by remember { mutableStateOf("") }
    var nit by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var acceptsDataPolicy by remember { mutableStateOf(false) }
    var acceptsTerms by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de organizador",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        InfoTextField(label = "Empresa", value = empresa, onValueChange = { empresa = it })
        Spacer(modifier = Modifier.height(12.dp))
        InfoTextField(label = "NIT", value = nit, onValueChange = { nit = it }, keyboardType = KeyboardType.Number)
        Spacer(modifier = Modifier.height(12.dp))
        InfoTextField(label = "Número", value = numero, onValueChange = { numero = it }, keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(12.dp))
        InfoTextField(label = "Dirección", value = direccion, onValueChange = { direccion = it })
        Spacer(modifier = Modifier.height(12.dp))
        InfoTextField(label = "Email", value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Campo de contraseña con icono para mostrar/ocultar
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(icon, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFF9A825),
                unfocusedIndicatorColor = Color.LightGray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AcceptanceRow(
            text = "Acepto el uso de tratamiento de datos.",
            checked = acceptsDataPolicy,
            onCheckedChange = { acceptsDataPolicy = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        AcceptanceRow(
            text = "Acepto políticas de uso.",
            checked = acceptsTerms,
            onCheckedChange = { acceptsTerms = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (!acceptsDataPolicy || !acceptsTerms) {
                    Toast.makeText(context, "Debes aceptar las políticas", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (empresa.isBlank() || nit.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true

                // 🔹 Crear usuario en Firebase Authentication
                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnSuccessListener { result ->
                        val userId = result.user?.uid ?: return@addOnSuccessListener

                        // 🔹 Guardar datos adicionales en Firestore
                        organizerViewModel.saveProfile(
                            userId = userId,
                            empresa = empresa,
                            nit = nit,
                            numero = numero,
                            direccion = direccion,
                            email = email,
                            password = password
                        ) { success, error ->
                            loading = false
                            if (success) {
                                Toast.makeText(context, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show()
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        Toast.makeText(context, "Error al registrar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            },
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF9A825),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text("Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

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
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color(0xFFF9A825),
            unfocusedIndicatorColor = Color.LightGray
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction)
    )
}

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
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}
