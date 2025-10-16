package com.planapp.qplanzaso

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme
import com.planapp.qplanzaso.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MyApp", "Firebase inicializado correctamente ✅")

        enableEdgeToEdge()
        setContent {
            QPlanzasoTheme {
                Scaffold { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

