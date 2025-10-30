package com.planapp.qplanzaso.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1800) // 1.8s o el tiempo que quieras
        navController.navigate("location_permission") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_qplanzaso),
                contentDescription = "Logo",
                modifier = Modifier.size(280.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    QPlanzasoTheme {
        // Se crea un NavController “falso” solo para previsualización
        val navController = rememberNavController()
        SplashScreen(navController)
    }
}
