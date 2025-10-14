package com.planapp.qplanzaso.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.R

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    //contexto para llamar metodos de autenticacion

    val auth = FirebaseAuth.getInstance()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF7A00))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Imagen circular superior
            Image(
                painter = painterResource(id = R.drawable.img2), // tu imagen
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Bienvenido de nuevo!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // === Campo Usuario ===
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Usuario",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                BasicTextField(
                    value = username,
                    onValueChange = { username = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    decorationBox = { innerTextField ->
                        Column {
                            innerTextField()
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.White.copy(alpha = 0.8f))
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === Campo Clave ===
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Clave",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    decorationBox = { innerTextField ->
                        Column {
                            innerTextField()
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.White.copy(alpha = 0.8f))
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // === Texto “Olvidé mi clave” alineado a la derecha ===
            TextButton(
                onClick = { navController.navigate("forgot") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Olvidé mi clave", color = Color(0xFF0033CC), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // === Botón Acceder ===
            Button(

                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        loading = true

                        auth.signInWithEmailAndPassword(username, password)

                            .addOnCompleteListener { task ->
                                loading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Inicio de sesión exitoso ✅",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("home") {
                                        //val currentUser = auth.currentUser


                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Credenciales inválidas ❌",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB866),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(if (loading) "Cargando..." else "Acceder", fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    val fakeNavController = rememberNavController()
    LoginScreen(navController = fakeNavController)
}
