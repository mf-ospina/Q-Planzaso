package com.planapp.qplanzaso.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun TipoOrganizadorScreen(
    navController: NavController
) {
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF7A00))
            .padding(horizontal = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¿Eres organizador?",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ayúdanos a saber quién eres.",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))

            if (loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                // === Botón Empresarial ===
                Button(
                    onClick = {
                        // VERIFICA ESTA LÍNEA EXACTAMENTE
                        navController.navigate("MoreInfoScreen")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDE4B5),
                        contentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Empresarial", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // === Botón Particular ===
                Button(
                    onClick = {
                        // Y VERIFICA ESTA OTRA LÍNEA EXACTAMENTE
                        navController.navigate("MoreInfoScreen")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDE4B5),
                        contentColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Particular", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipoOrganizadorScreen() {
    val fakeNavController = rememberNavController()
    TipoOrganizadorScreen(navController = fakeNavController)
}