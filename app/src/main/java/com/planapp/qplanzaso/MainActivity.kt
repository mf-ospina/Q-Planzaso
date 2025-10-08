package com.planapp.qplanzaso

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme
import com.planapp.qplanzaso.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MyApp", "Firebase inicializado correctamente✅")

        val db = FirebaseFirestore.getInstance()
        val testUser = hashMapOf("nombre" to "Kevin", "rol" to "Tester")

        db.collection("usuarios").add(testUser)
            .addOnSuccessListener { Log.d("Firestore", "✅ Documento agregado con ID: ${it.id}") }
            .addOnFailureListener { e -> Log.e("Firestore", "❌ Error al agregar documento", e) }

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
