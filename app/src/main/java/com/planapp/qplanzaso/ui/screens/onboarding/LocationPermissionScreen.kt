package com.planapp.qplanzaso.ui.screens.onboarding
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkButton
import com.planapp.qplanzaso.ui.theme.LightButton
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

    // Estado del diálogo
    var showDialog by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val locationGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        // Para Android 13+ miramos también notificaciones, pero no bloqueamos el flujo por eso
        val notificationsGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else {
                true
            }

        hasPermission = locationGranted

        if (locationGranted) {
            fusedClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    dialogMessage = context.getString(R.string.location_success_message)
                    isSuccess = true
                } else {
                    dialogMessage = context.getString(R.string.location_null_message)
                    isSuccess = false
                }
                showDialog = true
            }.addOnFailureListener {
                dialogMessage = context.getString(R.string.location_error_message)
                isSuccess = false
                showDialog = true
            }

            // Aquí podrías loguear si notificationsGranted es false, pero no es obligatorio
        } else {
            dialogMessage = context.getString(R.string.location_denied_message)
            isSuccess = false
            showDialog = true
        }
    }


    // Diálogo de resultado
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("account_choice") {
                            popUpTo("location") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccess) Color(0xFFF9A825) else Color.Red
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.close_button),
                        color = Color.White
                    )
                }
            },
            title = {
                Text(
                    text = if (isSuccess)
                        stringResource(R.string.dialog_success_title)
                    else
                        stringResource(R.string.dialog_error_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isSuccess) Color(0xFFF9A825) else Color.Red
                )
            },
            text = {
                Text(
                    text = dialogMessage,
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Contenido principal
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
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
                    contentDescription = stringResource(R.string.map_description),
                    tint = Color.White,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.location_title),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.location_subtitle),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = {
                        val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            perms.add(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        launcher.launch(perms.toTypedArray())
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkButton,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.location_allow_button))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        navController.navigate("account_choice") {
                            popUpTo("location") { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightButton,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.location_deny_button))
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
