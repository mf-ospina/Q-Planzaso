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

        //VERIFICACIÓN DE CONEXIÓN A FIREBASE
        val db = FirebaseFirestore.getInstance()

        // Escribir un documento de prueba
        val testData = hashMapOf(
            "nombre" to "Kevin",
            "mensaje" to "Conexión con Firestore exitosa 🚀"
        )

        db.collection("pruebas")
            .add(testData)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreTest", "Documento agregado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreTest", "Error al agregar documento", e)
            }

        // Leer todos los documentos de la colección "pruebas"
        db.collection("pruebas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("FirestoreTest", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreTest", "Error al leer documentos", e)
            }


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
