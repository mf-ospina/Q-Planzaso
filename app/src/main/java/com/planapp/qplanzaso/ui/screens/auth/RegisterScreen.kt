package com.planapp.qplanzaso.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.auth.AuthResult
import com.planapp.qplanzaso.auth.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var repetirClave by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var acceptsDataPolicy by remember { mutableStateOf(false) }
    var acceptsTerms by remember { mutableStateOf(false) }

    // 👇 Nueva variable para controlar el diálogo
    var showPolicyDialog by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    // Control de resultado de autenticación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.register_success),
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigate("login") {
                    popUpTo("RegisterScreen") { inclusive = true }
                }
            }

            is AuthResult.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthResult.Error).message,
                    Toast.LENGTH_LONG
                ).show()
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
                text = context.getString(R.string.register_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.img2),
                contentDescription = stringResource(R.string.tipo_organizador_image_desc),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            StyledTextField(label = context.getString(R.string.label_nombre), value = nombre, onValueChange = { nombre = it })
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = context.getString(R.string.label_apellido), value = apellido, onValueChange = { apellido = it })
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = context.getString(R.string.label_usuario), value = usuario, onValueChange = { usuario = it })
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = context.getString(R.string.label_clave), value = clave, onValueChange = { clave = it }, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = context.getString(R.string.label_repetir_clave), value = repetirClave, onValueChange = { repetirClave = it }, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(
                label = context.getString(R.string.label_email),
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Filas de aceptación con botón de ver políticas
            AcceptanceRow(
                text = context.getString(R.string.accept_data_policy),
                checked = acceptsDataPolicy,
                onCheckedChange = { acceptsDataPolicy = it }
            )
            AcceptanceRow(
                text = context.getString(R.string.accept_terms),
                checked = acceptsTerms,
                onCheckedChange = { acceptsTerms = it },
                onSeePolicyClick = { showPolicyDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (clave != repetirClave) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_passwords_mismatch),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (nombre.isBlank() || apellido.isBlank() || email.isBlank()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_empty_fields),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.register(
                            email = email.trim(),
                            password = clave.trim(),
                            nombre = "$nombre $apellido".trim(),
                            tipoUsuario = "particular"
                        )
                    }
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
                    Text(
                        text = context.getString(R.string.continue_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 👇 Diálogo modal para ver las políticas
        if (showPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPolicyDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.privacy_policy_title),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF9A825)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.privacy_policy_content),
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPolicyDialog = false }) {
                        Text(stringResource(R.string.back_button), color = Color(0xFFF9A825))
                    }
                },
                containerColor = Color.White
            )
        }
    }
}


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
            focusedIndicatorColor = Color(0xFFF9A825),
            unfocusedIndicatorColor = Color.LightGray,
            focusedLabelColor = Color.DarkGray,
            unfocusedLabelColor = Color.Gray
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        )
    )
}

@Composable
private fun AcceptanceRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onSeePolicyClick: (() -> Unit)? = null
) {
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
        Text(text, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        if (onSeePolicyClick != null) {
            TextButton(onClick = onSeePolicyClick) {
                Text(
                    text = stringResource(R.string.see_policies),
                    color = Color(0xFFF9A825),
                    fontSize = 14.sp
                )
            }
        }
    }
}

