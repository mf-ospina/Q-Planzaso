package com.planapp.qplanzaso.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.theme.DarkButton

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Control del diálogo
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var dialogTitle by remember { mutableStateOf("") }

    //Para el teclado
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_min),
                    contentDescription = stringResource(R.string.login_avatar_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.login_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.login_user_label),
                        color = DarkButton,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    BasicTextField(
                        value = username,
                        onValueChange = { username = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color(0xFF333333), fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                        ),
                        decorationBox = { innerTextField ->
                            Column {
                                innerTextField()
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(DarkButton)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.login_password_label),
                        color = DarkButton,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    BasicTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(color = Color(0xFF333333), fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // 🔹 Al presionar “Enter” se intenta iniciar sesión
                                if (username.isNotEmpty() && password.isNotEmpty()) {
                                    loading = true
                                    auth.signInWithEmailAndPassword(username, password)
                                        .addOnCompleteListener { task ->
                                            loading = false
                                            if (task.isSuccessful) {
                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                dialogTitle = context.getString(R.string.login_invalid)
                                                dialogMessage = context.getString(R.string.login_invalid_text)
                                                showDialog = true
                                            }
                                        }
                                } else {
                                    dialogTitle = context.getString(R.string.login_empty_fields)
                                    dialogMessage = context.getString(R.string.login_empty_fields)
                                    showDialog = true
                                }
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Column {
                                innerTextField()
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(DarkButton)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.navigate("forgot") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        stringResource(R.string.login_forgot_password),
                        color = Color(0xFF0A3D91),
                        fontSize = 13.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            loading = true
                            auth.signInWithEmailAndPassword(username, password)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        dialogTitle = context.getString(R.string.login_invalid)
                                        dialogMessage = context.getString(R.string.login_invalid_text)
                                        showDialog = true
                                    }
                                }
                        } else {
                            dialogTitle = context.getString(R.string.login_empty_fields)
                            dialogMessage = context.getString(R.string.login_empty_fields)
                            showDialog = true
                        }
                    },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkButton,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        if (loading)
                            stringResource(R.string.login_loading)
                        else
                            stringResource(R.string.login_acceder),
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Modal de error
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(dialogTitle, fontWeight = FontWeight.Bold, color = Color.Black) },
                text = { Text(dialogMessage, color = Color.Gray) },
                containerColor = Color.White,
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.close_button), color = DarkButton)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    val fakeNavController = rememberNavController()
    LoginScreen(navController = fakeNavController)
}
