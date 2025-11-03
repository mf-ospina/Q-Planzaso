package com.planapp.qplanzaso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.ui.navigation.AppNavigation
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QPlanzasoTheme {
                val navController = rememberNavController()
                AppNavigation(
                    modifier = Modifier,
                    navController = navController,
                    screenToOpen = null // o "notificaciones" si quieres abrir Notificaciones al inicio
                )
            }
        }
    }
}
