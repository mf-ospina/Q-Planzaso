package com.planapp.qplanzaso.ui.screens.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme

@Composable
fun LocationPermissionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("Location", "Lat:${location.latitude} Lon:${location.longitude}")
                } else {
                    Log.d("Location", "Ubicación null (a veces ocurre en emulador)")
                }
                // 👉 Si acepta el permiso, sigue el flujo normal
                navController.navigate("account_choice") {
                    popUpTo("location") { inclusive = true }
                }
            }.addOnFailureListener {
                Log.w("Location", "Error al obtener ubicación", it)
                navController.navigate("account_choice") {
                    popUpTo("location") { inclusive = true }
                }
            }
        } else {
            Log.d("Location", "Permiso denegado")
            // 👉 Si niega el permiso, igual continúa el flujo
            navController.navigate("account_choice") {
                popUpTo("location") { inclusive = true }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFF9A3C))
            .padding(28.dp),
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
                Icon(
                    painter = painterResource(id = R.drawable.map),
                    contentDescription = "Mapa",
                    tint = Color.White,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Encuentra planes cerca de ti",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Necesitamos tu ubicación para mostrar eventos en tu zona",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Permitir ubicación")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        // 👉 Si el usuario no da permiso, igual sigue al flujo normal
                        navController.navigate("account_choice") {
                            popUpTo("location") { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ahora no")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLocationPermissionScreen() {
    QPlanzasoTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        LocationPermissionScreen(navController = navController)
    }
}
