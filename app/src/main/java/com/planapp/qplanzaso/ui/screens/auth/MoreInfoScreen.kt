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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.auth.AuthResult
import com.planapp.qplanzaso.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    // Estado del BottomSheet
    var showPolicySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estados para diálogos
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    // Leer archivo de políticas desde assets
    val policyText by remember {
        mutableStateOf(
            try {
                context.assets.open("politicas.txt").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.getString(R.string.policy_error)
            }
        )
    }

    val authState by viewModel.authState.collectAsState()

    // Manejo del resultado de autenticación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> showSuccessDialog = true
            is AuthResult.Error -> showErrorDialog = (authState as AuthResult.Error).message
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
            horizontalAlignment = Alignment.CenterHorizontally
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
                contentDescription = stringResource(R.string.moreinfo_image_desc),
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

            // Campo de contraseña
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
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
                onCheckedChange = { acceptsTerms = it },
                onSeePolicyClick = { showPolicySheet = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!acceptsDataPolicy || !acceptsTerms) {
                        Toast.makeText(context, context.getString(R.string.moreinfo_accept_policies_warning), Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (empresa.isBlank() || nit.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.moreinfo_fill_all_warning), Toast.LENGTH_SHORT).show()
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
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.moreinfo_continue), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // BottomSheet con políticas
        if (showPolicySheet) {
            ModalBottomSheet(
                onDismissRequest = { showPolicySheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.9f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.privacy_policy_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFFF9A825)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = policyText,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showPolicySheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825))
                    ) {
                        Text(stringResource(R.string.close_button), color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Diálogo de éxito
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate("login") {
                                popUpTo("MoreInfoScreen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825))
                    ) {
                        Text(stringResource(R.string.close_button), color = Color.White)
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.register_success),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFFF9A825)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.moreinfo_success_message),
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Diálogo de error
        showErrorDialog?.let { errorMessage ->
            AlertDialog(
                onDismissRequest = { showErrorDialog = null },
                confirmButton = {
                    Button(
                        onClick = { showErrorDialog = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825))
                    ) {
                        Text(stringResource(R.string.close_button), color = Color.White)
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.error_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Red
                    )
                },
                text = {
                    Text(
                        text = errorMessage,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

// ─────────────────────────────
// COMPONENTES REUTILIZABLES
// ─────────────────────────────

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
