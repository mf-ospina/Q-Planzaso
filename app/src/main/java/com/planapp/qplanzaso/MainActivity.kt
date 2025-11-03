package com.planapp.qplanzaso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.ui.navigation.AppNavigation
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel

class MainActivity : ComponentActivity() {

    private val eventoViewModel: EventoViewModel by viewModels()

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

    override fun onResume() {
        super.onResume()
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        eventoViewModel.verificarEventosProximosYRecordar(this, usuarioId)
    }
}
