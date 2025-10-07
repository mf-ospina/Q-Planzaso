package com.planapp.qplanzaso

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.firestore.FirebaseFirestore
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QPlanzasoTheme {
        Greeting("Android")
    }
}