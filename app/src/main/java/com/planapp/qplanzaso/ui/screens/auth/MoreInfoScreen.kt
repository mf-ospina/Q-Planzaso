package com.planapp.qplanzaso.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.auth.AuthResult
import com.planapp.qplanzaso.auth.AuthViewModel

@Composable
fun MoreInfoScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var empresa by remember { mutableStateOf("") }
    var nit by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var acceptsDataPolicy by remember { mutableStateOf(false) }
    var acceptsTerms by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    // Control de resultado de autenticación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> {
                Toast.makeText(context, "Empresa registrada correctamente ✅", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo("MoreInfoScreen") { inclusive = true }
                }
            }

            is AuthResult.Error -> {
                Toast.makeText(context, (authState as AuthResult.Error).message, Toast.LENGTH_LONG).show()
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.moreinfo_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.img3),
                contentDescription = stringResource(R.string.tipo_organizador_image_desc),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            StyledTextField(label = stringResource(R.string.moreinfo_empresa), value = empresa, onValueChange = { empresa = it })
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = stringResource(R.string.moreinfo_nit), value = nit, onValueChange = { nit = it }, keyboardType = KeyboardType.Number)
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = stringResource(R.string.moreinfo_numero), value = numero, onValueChange = { numero = it }, keyboardType = KeyboardType.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = stringResource(R.string.moreinfo_direccion), value = direccion, onValueChange = { direccion = it })
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = stringResource(R.string.moreinfo_email), value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña con icono de visibilidad
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.moreinfo_password)) },
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
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFF9A825),
                    unfocusedIndicatorColor = Color.LightGray,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            AcceptanceRow(
                text = stringResource(R.string.moreinfo_data_policy),
                checked = acceptsDataPolicy,
                onCheckedChange = { acceptsDataPolicy = it }
            )

            AcceptanceRow(
                text = stringResource(R.string.moreinfo_terms),
                checked = acceptsTerms,
                onCheckedChange = { acceptsTerms = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

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

                    viewModel.register(
                        email = email.trim(),
                        password = password.trim(),
                        nombre = empresa.trim(),
                        tipoUsuario = "empresarial"
                    )
                },
                enabled = acceptsDataPolicy && acceptsTerms && authState !is AuthResult.Loading,
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
                if (authState is AuthResult.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(stringResource(R.string.moreinfo_continue), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StyledTextField(
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
            focusedIndicatorColor = Color(0xFFF9A825),
            unfocusedIndicatorColor = Color.LightGray,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.Gray
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMoreInfoScreen() {
    val fakeNavController = rememberNavController()
    MoreInfoScreen(navController = fakeNavController)
}
